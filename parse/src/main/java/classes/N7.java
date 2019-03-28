package classes;

public class N7 extends Bopohik implements Member {

    @Override
    public void getName() {
        names.add("n");
    }

    public static void main(String[] args) {
        int counter = 0;

        // внутренний класс в локальном методе
        class BDC {
        }

        // анонимный внутренний клас
        show(new Member() {
            @Override
            public void getName() {

            }
        });

    }

    private static void show(Member member) {
        member.getName();
    }

    @Override
    void sprositKakDela() {

    }

    // вложенный внутренний класс
    private class BDC {
        int members = 0;
    }

    // статичный вложенный внутренний класс
    static private class BDCJunior {
        int members = 0;

        public void show() {
            System.out.println(String.valueOf(members));
        }
    }

}

