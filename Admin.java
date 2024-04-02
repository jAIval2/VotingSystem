package voting;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class Admin extends JFrame {

    private JTextField passwordField;

    public Admin() {
        setTitle("Admin Login");
        setSize(300, 150);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        JLabel label = new JLabel("Enter Password:");
        passwordField = new JPasswordField();
        JButton loginButton = new JButton("Login");

        panel.add(label);
        panel.add(passwordField);
        panel.add(loginButton);

        add(panel);

        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String password = passwordField.getText();
                if (password.equals("hehe")) {
                    openAdminPanel();
                } else {
                    JOptionPane.showMessageDialog(Admin.this, "Incorrect password!", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
    }

    private void openAdminPanel() {
        AdminPanel adminPanel = new AdminPanel();
        adminPanel.setVisible(true);
        dispose(); // Close the login window
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new Admin().setVisible(true);
            }
        });
    }
}

class AdminPanel extends JFrame {

    public AdminPanel() {
        setTitle("Admin Panel");
        setSize(300, 150);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        JButton resultButton = new JButton("Result");
        JButton analyticsButton = new JButton("Analytics");
        JButton exitButton = new JButton("Exit");

        panel.add(resultButton);
        panel.add(analyticsButton);
        panel.add(exitButton);

        add(panel);

        resultButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showResult();
            }
        });

        analyticsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                performAnalytics();
            }
        });

        exitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Close the admin panel and return to the login page
                dispose();
                new VotingSystemGUI().setVisible(true); // Show the login page again
            }
        });
    }

    private void showResult() {
        try {
            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/voting_system", "root", "hehe");
            Statement statement = connection.createStatement();

            // Query to get total votes cast for each party
            String totalVotesQuery = "SELECT party_voted_for, COUNT(*) AS total_votes FROM voters GROUP BY party_voted_for";
            ResultSet totalVotesResult = statement.executeQuery(totalVotesQuery);

            int totalVotesCast = 0;
            while (totalVotesResult.next()) {
                totalVotesCast += totalVotesResult.getInt("total_votes");
            }

            // Query to get total votes for each party and calculate percentage
            String resultQuery = "SELECT party_voted_for, COUNT(*) AS party_votes, " +
                                 "ROUND((COUNT(*) * 100) / " + totalVotesCast + ", 2) AS vote_percentage " +
                                 "FROM voters GROUP BY party_voted_for";
            ResultSet resultSet = statement.executeQuery(resultQuery);

            StringBuilder resultText = new StringBuilder("<html>");
            while (resultSet.next()) {
                String party = resultSet.getString("party_voted_for");
                int partyVotes = resultSet.getInt("party_votes");
                double votePercentage = resultSet.getDouble("vote_percentage");
                resultText.append("<b>").append(party).append("</b>: ").append(partyVotes).append(" votes (").append(votePercentage).append("%)<br>");
            }
            resultText.append("</html>");

            JOptionPane.showMessageDialog(this, resultText.toString(), "Election Result", JOptionPane.INFORMATION_MESSAGE);

            totalVotesResult.close();
            resultSet.close();
            statement.close();
            connection.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
    private void performAnalytics() {
        try {
            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/voting_system", "root", "hehe");
            Statement statement = connection.createStatement();

            // Query to calculate average age
            String avgAgeQuery = "SELECT AVG(hashed_age) AS avg_age FROM voters";
            ResultSet avgAgeResult = statement.executeQuery(avgAgeQuery);
            
            double avgAge = 0;
            if (avgAgeResult.next()) {
                avgAge = avgAgeResult.getDouble("avg_age");
            }

            // Query to count voters in different age groups
            String ageGroupsQuery = "SELECT " +
                                    "SUM(CASE WHEN hashed_age BETWEEN 18 AND 25 THEN 1 ELSE 0 END) AS age_18_25_count, " +
                                    "SUM(CASE WHEN hashed_age BETWEEN 26 AND 35 THEN 1 ELSE 0 END) AS age_26_35_count, " +
                                    "SUM(CASE WHEN hashed_age BETWEEN 36 AND 50 THEN 1 ELSE 0 END) AS age_36_50_count, " +
                                    "SUM(CASE WHEN hashed_age > 50 THEN 1 ELSE 0 END) AS age_above_50_count " +
                                    "FROM voters";
            ResultSet ageGroupsResult = statement.executeQuery(ageGroupsQuery);

            int age18_25Count = 0, age26_35Count = 0, age36_50Count = 0, ageAbove50Count = 0;
            if (ageGroupsResult.next()) {
                age18_25Count = ageGroupsResult.getInt("age_18_25_count");
                age26_35Count = ageGroupsResult.getInt("age_26_35_count");
                age36_50Count = ageGroupsResult.getInt("age_36_50_count");
                ageAbove50Count = ageGroupsResult.getInt("age_above_50_count");
            }

            // Display analytics results
            String analyticsMessage = "Analytics Results:\n" +
                                      "Average Age: " + avgAge + "\n" +
                                      "Age 18-25 Count: " + age18_25Count + "\n" +
                                      "Age 26-35 Count: " + age26_35Count + "\n" +
                                      "Age 36-50 Count: " + age36_50Count + "\n" +
                                      "Age Above 50 Count: " + ageAbove50Count;

            JOptionPane.showMessageDialog(this, analyticsMessage, "Analytics", JOptionPane.INFORMATION_MESSAGE);

            avgAgeResult.close();
            ageGroupsResult.close();
            statement.close();
            connection.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

}
