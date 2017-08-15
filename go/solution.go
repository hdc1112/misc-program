package main

import "fmt"

func main() {
	grid := [][]int{{1, 0}}
	fmt.Println(islandPerimeter(grid))
}

func islandPerimeter(grid [][]int) int {
	if grid == nil || len(grid) == 0 || len(grid[0]) == 0 {
		return 0
	}
	r, c := len(grid), len(grid[0])
	v := make([][]bool, r)
	for i := 0; i < r; i++ {
		v[i] = make([]bool, c)
	}
	p := 0
	for i := 0; i < r; i++ {
		for j := 0; j < c; j++ {
			p += dfs(grid, v, r, c, i, j)
		}
	}
	return p
}

func dfs(grid [][]int, v [][]bool, r, c, i, j int) int {
	if i < 0 || i >= r || j < 0 || j >= c || grid[i][j] == 0 || v[i][j] {
		return 0
	}
	v[i][j] = true
	p := 0
	if i == 0 || grid[i-1][j] == 0 {
		p++
	}
	if j == 0 || grid[i][j-1] == 0 {
		p++
	}
	if i == r-1 || grid[i+1][j] == 0 {
		p++
	}
	if j == c-1 || grid[i][j+1] == 0 {
		p++
	}
	return p + dfs(grid, v, r, c, i-1, j) + dfs(grid, v, r, c, i+1, j) +
			dfs(grid, v, r, c, i, j-1) + dfs(grid, v, r, c, i, j+1)
}
