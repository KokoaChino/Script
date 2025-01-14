package tools;

import java.util.Arrays;


public class UnionFindSet { // 并查集

    public final int n;
    public int[] fa, rank;

    public UnionFindSet(final int n) {
        this.n = n;
        this.fa = new int[n];
        this.rank = new int[n];
        for (int i = 0; i < n; i++) fa[i] = i;
        Arrays.fill(rank, 1);
    }

    public int find(int x) { // 查找元素 x 所在集合的代表元素
        if (x == fa[x]) return x;
        fa[x] = find(fa[x]);
        return fa[x];
    }

    public void union(int i, int j) { // 合并元素 i 和 j 所在的集合
        int x = find(i), y = find(j);
        if (rank[x] <= rank[y]) fa[x] = y;
        else fa[y] = x;
        if (rank[x] == rank[y] && x != y) rank[y]++;
    }
}