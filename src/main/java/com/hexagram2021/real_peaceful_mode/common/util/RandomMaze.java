package com.hexagram2021.real_peaceful_mode.common.util;

import com.google.common.collect.Lists;

import java.util.Collections;
import java.util.List;
import java.util.Random;

public class RandomMaze {
	private final int length;
	private final int[] ftr;
	private final boolean[][] finalMap;
	private final List<Integer> edges;

	public RandomMaze(int length, long seed) {
		this.length = length;
		int lengthPixels = 2 * length + 1;
		int lengthEdges = length * (length - 1) * 2;
		this.ftr = new int[length * length];
		for(int i = 0; i < this.ftr.length; ++i) {
			this.ftr[i] = i;
		}
		this.finalMap = new boolean[lengthPixels][lengthPixels];
		for(int i = 0; i < lengthPixels; ++i) {
			for(int j = 0; j < lengthPixels; ++j) {
				this.finalMap[i][j] = isAirNode(i, j);
			}
		}
		Integer[] edges = new Integer[lengthEdges];
		for(int i = 0; i < lengthEdges; ++i) {
			edges[i] = i;
		}
		this.edges = Lists.newArrayList(edges);
		Random random = new Random(seed);
		Collections.shuffle(this.edges, random);
		this.finalMap[1][0] = this.finalMap[lengthPixels - 2][lengthPixels - 1] = true;
		this.kruskal();
	}

	//Union-find Set Data Structure
	private int findFtr(int node) {
		if(this.ftr[node] == node) {
			return node;
		}
		return this.ftr[node] = this.findFtr(this.ftr[node]);
	}

	//Minimum Spanning Tree by Kruskal Algorithm
	private void kruskal() {
		int l = 2 * this.length - 1;
		for (Integer edge : this.edges) {
			//decode edge (x, y, move) from edge number
			int x = edge / l;
			int y = edge % l;
			//1 for going down, 0 for going right
			int move = 0;
			if (y >= this.length - 1) {
				move = 1;
				y -= this.length - 1;
			}
			int from = x * this.length + y;
			int to = from + (move == 0 ? 1 : this.length);
			int f1 = this.findFtr(from);
			int f2 = this.findFtr(to);
			if (f1 != f2) {
				this.ftr[f1] = f2;
				this.finalMap[2 * x + 1 + move][2 * y + 2 - move] = true;
			}
		}
	}

	public boolean isAir(int mapIndexX, int mapIndexY) {
		return this.finalMap[mapIndexX][mapIndexY];
	}

	public static boolean isWallNode(int mapIndexX, int mapIndexY) {
		return (mapIndexX & 1) == 0 && (mapIndexY & 1) == 0;
	}

	public static boolean isAirNode(int mapIndexX, int mapIndexY) {
		return (mapIndexX & 1) == 1 && (mapIndexY & 1) == 1;
	}

	public boolean isDeadEnd(int mapIndexX, int mapIndexY) {
		if(!isAirNode(mapIndexX, mapIndexY)) {
			return false;
		}
		int cnt = 0;
		if(this.isAir(mapIndexX - 1, mapIndexY)) {
			cnt += 1;
		}
		if(this.isAir(mapIndexX + 1, mapIndexY)) {
			cnt += 1;
		}
		if(this.isAir(mapIndexX, mapIndexY - 1)) {
			cnt += 1;
		}
		if(this.isAir(mapIndexX, mapIndexY + 1)) {
			cnt += 1;
		}
		return cnt == 3;
	}
}
