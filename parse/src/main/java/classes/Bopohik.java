package classes;

public abstract class Bopohik implements Member {

    public final static int randomNumber;
    static {
        randomNumber = 100;
    }


    public Bopohik() {
        // для задания общих значений переменных
    }


    void pokakat() {
        System.out.println("pook");
    }

    static void pook() {

    }

    abstract void sprositKakDela();

}
