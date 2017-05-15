package main

import "fmt"

func canIWin(maxChoosableInteger int, desiredTotal int) bool {
	if desiredTotal <= maxChoosableInteger {
		return true
	}
	if (1 + maxChoosableInteger) * maxChoosableInteger / 2 < desiredTotal {
		return false
	}
	return dfs(0, make(map[int]bool), maxChoosableInteger, desiredTotal)
}

func dfs(used int, cache map[int]bool, m, n int) bool {
	if val, exist := cache[used]; val && exist {
		return val
	}
	prog := 0
	for i := 0; i < m; i++ {
		mask := 1 << uint(i)
		if used & mask != 0 {
			prog += i + 1
		}
	}
	ret := false
	if prog >= n {
		ret = true
		goto exit
	}
	for i := 0; i < m; i++ {
		mask := 1 << uint(i)
		if used & mask == 0 && (prog + i + 1 >= n || !dfs(used | mask, cache, m, n)) {
			ret = true
			goto exit
		}
	}
exit:
	cache[used] = ret
	return ret
}

func main() {
	fmt.Println(canIWin(10, 11))
}