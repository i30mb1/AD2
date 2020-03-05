public class CustomException {

    public static void main(String[] args) {

    }

    private static void runSuske(int i) throws NotSasukeUser{
        if (i == 0) {
            System.out.println("ez");
        } else {
            throw new NotSasukeUser();
        }
    }

    public static class NotSasukeUser extends RuntimeException {

        String message;

        public NotSasukeUser() {
            message = "GDE SASUKE?";
        }

        public NotSasukeUser(String s) {
            this.message = s;
        }
//
        @Override
        public String toString() {
            return "i30mb1 custom exception : " + message;
        }
    }

}
