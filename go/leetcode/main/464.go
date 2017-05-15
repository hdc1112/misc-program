package main

import "fmt"

func canIWin(maxChoosableInteger int, desiredTotal int) bool {
	if desiredTotal <= maxChoosableInteger {
		return true
	}
	dp := make([]bool, desiredTotal + 1)
	for i, leftMostFalseIndex := desiredTotal, desiredTotal + 1; i >= 0; i-- {
		switch {
		case i + maxChoosableInteger >= desiredTotal:
			fallthrough
		case i + maxChoosableInteger >= leftMostFalseIndex:
			dp[i] = true
		default:
			dp[i], leftMostFalseIndex = false, i
		}
	}
	for i, v := range dp {
		fmt.Printf("%d(%v)\t", i, v)
	}
	return dp[0]
}

func main() {
	fmt.Println(canIWin(10, 40))
}