package main

import (
	"fmt"
	_ "reflect"
)

type A struct {
	val int
}

type B struct {
	A
}

type C struct {
	A
}

func main() {
	a1 := B{}
	a2 := C{}

	var x1 A = a1
	x2 :=

	array := [...]interface{}{a1, a2}

	for i, v := range array {
		fmt.Printf("%d %v %T\n", i, v, v)
	}
}