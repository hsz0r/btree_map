use rand::Rng;
use std::fmt;

struct Node<K, V>
where
    K: Ord,
    K: fmt::Display,
{
    keys: Vec<K>,
    values: Vec<V>,
    nodes: Vec<Node<K, V>>,
}

impl<K, V> Node<K, V>
where
    K: Ord,
    K: fmt::Display,
{
    fn new(t: usize) -> Node<K, V> {
        Node::<K, V> {
            keys: Vec::<K>::with_capacity(2 * t - 1),
            values: Vec::<V>::with_capacity(2 * t - 1),
            nodes: Vec::<Node<K, V>>::with_capacity(2 * t),
        }
    }
    fn isEmpty(&self) -> bool {
        self.keys.is_empty()
    }
    fn isLeaf(&self) -> bool {
        self.nodes.is_empty()
    }
    fn insert(&mut self, k: K, v: V, t: usize) -> bool {
        let pos = self.find_pos(&k);
        if self.isLeaf() {
            if pos.1 {
                self.keys[pos.0] = k;
                self.values[pos.0] = v;
                return false;
            }

            self.keys.insert(pos.0, k);
            self.values.insert(pos.0, v);
            return self.keys.len() == t;
        }

        let need_split = self.nodes[pos.0].insert(k, v, t);
        if need_split {
            self.split_child(pos.0, t);
            return self.keys.len() == t;
        }

        return false;
    }

    fn find(&self, k: &K) -> Option<&V> {
        let pos = self.find_pos(k);
        if pos.1 {
            return Some(&self.values[pos.0]);
        }

        if self.nodes.len() == 0 {
            return None;
        }

        return self.nodes[pos.0].find(k);
    }

    fn split_child(&mut self, pos: usize, t: usize) {
        let result = self.nodes[pos].split(t);
        self.keys.insert(pos, result.0);
        self.values.insert(pos, result.1);
        self.nodes.insert(pos + 1, result.2);
    }

    fn split(&mut self, t: usize) -> (K, V, Node<K, V>) {
        let mut right = Node::new(t);
        let mid = t / 2;

        right.keys = self.keys.split_off(mid + 1);
        right.values = self.values.split_off(mid + 1);

        let midE = self.keys.pop().unwrap();
        let midV = self.values.pop().unwrap();

        if self.nodes.len() > 0 {
            right.nodes = self.nodes.split_off(mid + 1);
        }

        return (midE, midV, right);
    }
    fn find_pos(&self, k: &K) -> (usize, bool) {
        let len = self.keys.len();

        if len > 7 && self.keys[len - 1] < *k {
            return (len, false);
        }

        if len < 7 {
            return self.lin_search(k, 0, len);
        }

        let mid = self.keys.len() / 2;
        if self.keys[mid] < *k {
            return self.lin_search(k, mid + 1, len);
        }
        if self.keys[mid] == *k {
            return (mid, true);
        }
        return self.rev_lin_search(k, 0, mid);
    }

    fn lin_search(&self, k: &K, start: usize, end: usize) -> (usize, bool) {
        for i in start..end {
            if self.keys[i] < *k {
                continue;
            }
            if self.keys[i] == *k {
                return (i, true);
            }
            return (i, false);
        }
        return (end, false);
    }

    fn rev_lin_search(&self, k: &K, start: usize, end: usize) -> (usize, bool) {
        for i in (start..end).rev() {
            if self.keys[i] < *k {
                return (i + 1, false);
            }
            if self.keys[i] == *k {
                return (i, true);
            }
        }
        return (0, false);
    }
}

struct Btree_map<K, V>
where
    K: Ord,
    K: fmt::Display,
{
    size: usize,
    height: usize,
    t: usize,
    root: Node<K, V>,
}

impl<K, V> Btree_map<K, V>
where
    K: Ord,
    K: fmt::Display,
{
    fn new() -> Btree_map<K, V> {
        Btree_map::<K, V>::new_with(2)
    }
    fn new_with(order: usize) -> Btree_map<K, V> {
        Btree_map::<K, V> {
            size: 0,
            height: 0,
            t: order,
            root: Node::<K, V>::new(order),
        }
    }
    fn isEmpty(&self) -> bool {
        self.root.isEmpty()
    }

    fn insert(&mut self, k: K, v: V) {
        self.size += 1;
        let need_split = self.root.insert(k, v, self.t);
        if need_split {
            let mut tmp = Node::new(self.t);
            std::mem::swap(&mut tmp, &mut self.root);
            let split = tmp.split(self.t);
            self.root.keys.push(split.0);
            self.root.values.push(split.1);
            self.root.nodes.push(tmp);
            self.root.nodes.push(split.2);
            self.height += 1;
        }
    }

    fn find(&self, k: &K) -> Option<&V> {
        return self.root.find(k);
    }

    fn getHeight(&self) -> usize {
        self.height
    }
    fn getSize(&self) -> usize {
        self.size
    }

    // fn test_height(&self) -> bool {
    //     return self.getHeight() <= logarithm((self.getSize() + 1) / 2) / logarithm(self.t);
    // }

    fn print(&self) {
        println!("{}", self.root);

        let mut next = Vec::<&Node<K, V>>::with_capacity(10);
        for n in &self.root.nodes {
            next.push(n);
        }

        loop {
            if next.len() == 0 {
                break;
            }

            for n in &next {
                print!("{} ", n);
            }
            println!();

            let mut tmp = Vec::<&Node<K, V>>::with_capacity(10);
            for n in &next {
                for c in &n.nodes {
                    tmp.push(c);
                }
            }

            std::mem::swap(&mut tmp, &mut next);
        }

        println!();
    }
}

impl<K, V> fmt::Display for Node<K, V>
where
    K: Ord,
    K: fmt::Display,
{
    fn fmt(&self, f: &mut fmt::Formatter) -> fmt::Result {
        write!(f, "[");
        if self.keys.len() > 0 {
            write!(f, "{}", self.keys[0]);
            for i in 1..self.keys.len() {
                write!(f, ",{}", self.keys[i]);
            }
        }
        write!(f, "]");
        Ok(())
    }
}

fn main() {
    // test1();
    test2();
}

fn test1() {
    let mut test = Btree_map::<i64, i64>::new_with(4);
    test.insert(30, 1);
    test.insert(70, 2);
    test.insert(8, 3);
    test.insert(25, 4);
    test.insert(40, 5);
    test.insert(50, 6);
    test.insert(76, 7);
    test.insert(88, 8);
    test.insert(1, 9);
    test.insert(3, 10);
    test.insert(7, 11);
    test.insert(15, 12);
    test.insert(21, 13);
    test.insert(23, 14);
    test.insert(26, 15);
    test.insert(28, 16);
    test.insert(35, 17);
    test.insert(38, 18);
    test.insert(42, 19);
    test.insert(49, 20);
    test.insert(56, 21);
    test.insert(67, 22);
    test.insert(71, 23);
    test.insert(73, 24);
    test.insert(75, 25);
    test.insert(77, 26);
    test.insert(85, 27);
    test.insert(89, 28);
    test.insert(97, 29);
    test.print();
    println!("t {}", &test.t);
    println!("Size {}", &test.getSize());

    println!("Height {}", &test.getHeight());
}

fn test2() {
    let mut b = Btree_map::<i64, i64>::new_with(3);
    let mut rng = rand::thread_rng();
    for x in 1..50 {
        b.insert(rng.gen_range(0..9999999999), x);
    }
    b.print();
    //b.find(&4);

    //println!("{:?}", &b.test_height());

    println!("Size {}", &b.getSize());

    println!("Height {}", &b.getHeight());
}
