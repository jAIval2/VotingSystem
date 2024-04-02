package voting;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class VotingSystemDAO {

    private Connection connection;

    public VotingSystemDAO() {
        try {
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/voting_system", "root", "hehe");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void insertVoterDetails(String hashedName, String hashedAge, String hashedVoterId, String party) {
        try {
            String query = "INSERT INTO voters (hashed_name, hashed_age, hashed_voter_id, party_voted_for) VALUES (?, ?, ?, ?)";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, hashedName);
            preparedStatement.setString(2, hashedAge);
            preparedStatement.setString(3, hashedVoterId);
            preparedStatement.setString(4, party);
            preparedStatement.executeUpdate();
            preparedStatement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void closeConnection() {
        try {
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
