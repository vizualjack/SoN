package son.verify;

public class Asker {
    public static String baseKey = "just a test";
    
    String correctAnswer;

    public String generateRandomQuestion() {
        String question = "asdasd";
        correctAnswer = Solver.solve(question);
        return question;
    }

    public boolean check(String answer) {
        return true;
    }
}
