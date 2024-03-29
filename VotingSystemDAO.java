package voting;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class VotingSystemDAO {
    private DBManager dbManager;

    public VotingSystemDAO() {
        dbManager = new DBManager();
    }

    public void insertVoterDetails(String hashedAge, String hashedVoterId, String party) {
        try {
            Connection connection = dbManager.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO voters (hashed_age, hashed_voter_id, party_voted_for) VALUES (?, ?, ?)");
            preparedStatement.setString(1, hashedAge);
            preparedStatement.setString(2, hashedVoterId);
            preparedStatement.setString(3, party);
            preparedStatement.executeUpdate();
            preparedStatement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void closeConnection() {
        dbManager.closeConnection();
    }
}
