// create bt from in-order & pre-order traversal

// lesson:
// 1. don't use [a,b], use [a,b), or a, length
//    be extremely careful with what is an index,
//    and what is a length.
// 2. if the program is hard to write, be sure to
//    write down some intermediate comments to help
//    yourself or draw a graph on paper
// 3. writing a correct program and debugging are two
//    different things. try your best to write correctly
//    at the first place

#include "treenode.h"
#include <stdio.h>
#include <stdlib.h>

pbtnode createbt(int[],int,int[],int);
pbtnode __createbt(int[],int,int,int[],int,int);

int main() {
  //int inorder[] = {1,2,3};
  //int preorder[] = {2,1,3};

  int inorder[] = {3,4,7,5,6};
  int preorder[] = {4,3,5,7,6};

  pbtnode r = createbt(inorder, sizeof(inorder) / sizeof(int), 
                        preorder, sizeof(preorder) / sizeof(int));

  in_order(r);
  pre_order(r);
  return 0;
}

// assumption:
// 1. the elements are distinctive from each other
pbtnode createbt(int inorder[], int ilen, int preorder[], int plen) {
  if ((ilen != plen) || (ilen == plen && ilen == 0))
    return NULL;
  return __createbt(inorder, 0, ilen, preorder, 0, plen);
}

// assumption:
// 1. the elements are distinctive from each other
// 2. the length are the same
//
// ins = inorder start index
// ilen = inorder length
// pres = preorder start index
// plen = preorder length
pbtnode __createbt(int inorder[], int ins, int ilen, 
                    int preorder[], int pres, int plen) {
  // not of the same length, illegal
  if (ilen != plen) return NULL; // illegal input

  // none node
  if (ilen == 0) return NULL;

  // only one node
  if (ilen == 1)  {
    if (inorder[ins] == preorder[pres]) {
        pbtnode r = (pbtnode)malloc(sizeof(btnode));
        INITDATA(r, inorder[ins]);
        return r;
      } else {return NULL;} // illegal input
  }
  
  // >1 node
  pbtnode r = (pbtnode)malloc(sizeof(btnode));
  // preorder's 1st ele is the root
  INITDATA(r, preorder[pres]);

  // find the root in in-order trace
  int i = 0;
  for (i = ins; i < ins + ilen; i++) {
    if (inorder[i] == preorder[pres])
      break;
  }
  if (i == ins + ilen) return NULL; // illegal input

  // now, the left subtree's node number is  i - ins
  // the right subtree's node number is ins + ilen - i - 1
  LEFT(r) = __createbt(inorder, ins, i - ins, preorder, pres + 1, i - ins);
  RIGHT(r) = __createbt(inorder, i + 1, ins + ilen - i - 1, preorder, pres + 1 + i - ins, ins + ilen - i - 1);
  return r;
}
