#ifndef TREENODE_H
#define TREENODE_H

#include <stdio.h>
#include <stdlib.h>

// lesson:
// 1. don't use typedef's name inside struct
// 2. don't write static func in .h, write inline static 
//    instead. 

typedef int btdata;

typedef struct btnode {
  btdata data;
  // don't use btnode *left, *right
  struct btnode *left, *right;
} btnode, *pbtnode;

#define DATA(pbtnode) ((pbtnode)->data)
#define LEFT(pbtnode) ((pbtnode)->left)
#define RIGHT(pbtnode) ((pbtnode)->right)
#define INIT(pbtnode) \
  do { \
    pbtnode->data = 0; \
    pbtnode->left = NULL; \
    pbtnode->right = NULL; \
  } while(0)

#define INITDATA(node, d) \
  do { \
    node->data = d; \
    node->left = NULL; \
    node->right = NULL; \
  } while(0)

// test cases
inline static pbtnode case_balancebt() {
  pbtnode p1 = (pbtnode)malloc(sizeof(btnode));
  pbtnode p2 = (pbtnode)malloc(sizeof(btnode));
  pbtnode p3 = (pbtnode)malloc(sizeof(btnode));
  pbtnode p4 = (pbtnode)malloc(sizeof(btnode));
  INIT(p1);
  INIT(p2);
  INIT(p3);
  INIT(p4);
  LEFT(p1) = p2;
  RIGHT(p1) = p3;
  LEFT(p2) = p4;

  return p1;
}

inline static pbtnode case_imbalancebt() {
  pbtnode p1 = (pbtnode)malloc(sizeof(btnode));
  pbtnode p2 = (pbtnode)malloc(sizeof(btnode));
  pbtnode p3 = (pbtnode)malloc(sizeof(btnode));
  pbtnode p4 = (pbtnode)malloc(sizeof(btnode));
  INIT(p1);
  INIT(p2);
  INIT(p3);
  INIT(p4);
  LEFT(p1) = p2;
  RIGHT(p2) = p3;
  LEFT(p3) = p4;

  return p1;
}

inline static void in_order(pbtnode r) {
  if (r != NULL) {
    in_order(LEFT(r));
    printf("%d ", DATA(r));
    in_order(RIGHT(r));
  }
}

inline static void pre_order(pbtnode r) {
  if (r != NULL) {
    printf("%d ", DATA(r));
    pre_order(LEFT(r));
    pre_order(RIGHT(r));
  }
}
#endif
