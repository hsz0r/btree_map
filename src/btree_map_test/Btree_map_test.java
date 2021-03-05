/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package btree_map_test;

import static java.lang.Math.log;



/**
 *
 * @author hsz0r
 */
public class Btree_map_test {

    /**
     * @param args the command line arguments
     */
    
  public static void main(String[] args) {
    btree_map test = new btree_map(3);

    System.out.println("Пусто? " + test.isEmpty());
    long before = System.currentTimeMillis();
    for(int i = 1; i < 100; i++){
        test.add(i, i);
    }
    long after = System.currentTimeMillis();
    long result = after - before;
    System.out.println("Time: " + result);
    //System.out.println(test.nodeTest());
    
    System.out.println("Пусто? " + test.isEmpty());
    System.out.println("Contains? " + test.searchpair(111).toString());
    //System.out.println(test.heightTest());
   
//    b.clear();
//    System.out.println("Пусто? " + b.isEmpty());
//    btree_map test1 = new btree_map(test);
//    System.out.println(test1.nodeTest());
//    System.out.println("Пусто? " + test1.isEmpty());
//    System.out.println(test1.heightTest());
  }
}
