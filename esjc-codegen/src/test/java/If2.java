public class If2 {
    public static void main(String[] args) {

    }

    static int foo1(int x, int y) {
        int z;
        z = 0;
        if (x > y) { z = x; }
        return z;
    }

    static int foo2(int x, int y) {
        int z;
        z = 0;
        if (x > y) { z = x; }
        else { z = y; }
        return z;
    }
}
