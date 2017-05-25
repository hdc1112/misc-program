package main

import (
	"fmt"
)

func getMaxRepetitions(s1 string, n1 int, s2 string, n2 int) int {
	if s1 == "" || n1 == 0 || s2 == "" || n2 == 0 {
		return 0
	}
	var matcheds2, p1, p2 int
	s1incs2 := make(map[int]int)
	p2taken := make(map[int]bool)
	for p1 < len(s1) * n1 {
		ch1, ch2 := s1[p1 % len(s1)], s2[p2 % len(s2)]
		if ch1 == ch2 {
			p2++
		}
		p1++
		if p2 % len(s2) == 0 && p2 > 0 && !p2taken[p2] {
			matcheds2++
			p2taken[p2] = true
		}
		if p1 % len(s1) == 0 {
			s1incs2[p1 / len(s1)] = matcheds2
		}
		if p1 % len(s1) == 0 && p2 % len(s2) == 0 {
			break
		}
	}
	if p2 % len(s2) != 0 {
		fmt.Println("haha")
		return matcheds2 / n2
	}
	x, y := p1 / len(s1), matcheds2
	return (n1 / x * y + s1incs2[n1 % x]) / n2
}

func main() {
	fmt.Println(getMaxRepetitions("phqghumeaylnlfdxfircvscxggbwkfnqduxwfnfozvsrtkjpre", 1000000, "pggxr", 100))
}