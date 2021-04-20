import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AuthorDAOImpl implements AuthorDAO
{
  private static AuthorDAOImpl instance;

  private AuthorDAOImpl() throws SQLException
  {
    DriverManager.registerDriver(new org.postgresql.Driver());
  }

  public static synchronized AuthorDAOImpl getInstance() throws SQLException {
    if (instance == null) {
      instance = new AuthorDAOImpl();
    }
    return instance;
  }

  private Connection getConnection() throws SQLException
  {
    return DriverManager.getConnection(
        "jdbc:postgresql://localhost:5432/postgres?currentSchema=jdbc",
        "postgres", "noA09Eigp5vj");
  }

  @Override public Author create(int id, String name) throws SQLException
  {
    try(Connection connection = getConnection()) {
      PreparedStatement statement = connection.prepareStatement("INSERT "
          + "INTO Author(name) VALUES (?)", PreparedStatement.RETURN_GENERATED_KEYS);
      statement.setString(1,name);
      statement.executeUpdate();
      ResultSet keys = statement.getGeneratedKeys();
      if(keys.next()) {
        return new Author(keys.getInt(1), name);
      } else {
        throw new SQLException("No keys generated");
      }
    }
  }

  @Override public Author readById(int id) throws SQLException
  {
     try(Connection connection = getConnection()) {
       PreparedStatement statement = connection
           .prepareStatement("SELECT * FROM Author WHERE id = ?");
       statement.setInt(1, id);
       ResultSet resultSet = statement.executeQuery();
       if(resultSet.next()) {
         String name = resultSet.getString("name");
         return new Author(id, name);
       } else {
         return null;
       }
     }
  }

  @Override public List<Author> readByName(String searchString)
      throws SQLException
  {
    try(Connection connection = getConnection()) {
      PreparedStatement statement = connection
          .prepareStatement("SELECT * FROM Author WHERE name LIKE ?");
      statement.setString(1, "%" + searchString + "%");
      ResultSet resultSet = statement.executeQuery();
      ArrayList<Author> result = new ArrayList<>();
      while(resultSet.next()) {
        int id = resultSet.getInt("id");
        String name = resultSet.getString("name");
        Author author = new Author(id, name);
        result.add(author);
      }
      return result;
    }
  }

  @Override public void update(Author author) throws SQLException
  {
    try(Connection connection = getConnection())
    {
      PreparedStatement statement = connection.prepareStatement(
          "UPDATE Author SET name = ? WHERE id = ?");
      statement.setString(1, author.getName());
      statement.setInt(2, author.getId());
      statement.executeUpdate();
    }
  }

  @Override public void delete(Author author) throws SQLException
  {
    try(Connection connection = getConnection())
    {
      PreparedStatement statement = connection.prepareStatement(
          "DELETE FROM Author WHERE id = ?");
      statement.setInt(1, author.getId());
      statement.executeUpdate();
    }
  }
}
