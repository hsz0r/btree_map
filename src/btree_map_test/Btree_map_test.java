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
    btree_map b = new btree_map(7);

    System.out.println("Пусто? " + b.isEmpty());
    long before = System.currentTimeMillis();
    for(int i = 1; i < 1000000; i++){
        b.add(i, i);
    }
    long after = System.currentTimeMillis();
    long result = after - before;
    System.out.println("Time: " + result);
    System.out.println(b.nodeTest());
    
    System.out.println("Пусто? " + b.isEmpty());
    System.out.println(b.heightTest());
   
    b.clear();
    System.out.println("Пусто? " + b.isEmpty());
  }
    
}
