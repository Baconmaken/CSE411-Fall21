class C {
    public int f;
}

public class Assignment {

    public static void main(String[] args) {

    }

    static void foo1(int x, int y) {
        C c;
        x = 1;
        y = 2;
        c = new C();
        c.f = 4;
    }
}
