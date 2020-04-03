package chess.domain.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import chess.domain.dto.TurnDto;

public class TurnDao {
	private Connection connection;

	public TurnDao(Connection connection) {
		this.connection = connection;
	}

	public void addTurn(TurnDto turnDto) throws SQLException {
		String query = "INSERT INTO state VALUES (1, ?)";
		PreparedStatement statement =connection.prepareStatement(query);
		statement.setString(1, turnDto.getColorName());
		statement.executeUpdate();
	}

	public TurnDto findTurn() throws SQLException {
		String query = "SELECT * FROM state WHERE id = 1";
		PreparedStatement statement = connection.prepareStatement(query);
		ResultSet result = statement.executeQuery();

		if (!result.next()) {
			return null;
		}

		String nowTurn = result.getString("turn");

		return TurnDto.of(nowTurn);
	}

	public void editTurn(TurnDto turnDto) throws SQLException {
		String query = "INSERT INTO state VALUE (1, ?) ON DUPLICATE KEY UPDATE turn = ?";
		PreparedStatement statement = connection.prepareStatement(query);
		statement.setString(1, turnDto.getColorName());
		statement.setString(2, turnDto.getColorName());
		statement.executeUpdate();
	}
}
