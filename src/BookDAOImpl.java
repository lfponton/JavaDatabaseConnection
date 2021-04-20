import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BookDAOImpl implements BookDAO
{
  private static BookDAOImpl instance;

  private BookDAOImpl() throws SQLException {
    DriverManager.registerDriver(new org.postgresql.Driver());
  }

  public static synchronized BookDAOImpl getInstance() throws SQLException {
    if (instance == null) {
      instance = new BookDAOImpl();
    }
    return instance;
  }

  private Connection getConnection() throws SQLException
  {
    return DriverManager.getConnection(
        "jdbc:postgresql://localhost:5432/postgres?currentSchema=jdbc",
        "postgres", "noA09Eigp5vj");
  }

  @Override public Book create(String isbn, String title, int yearOfPublishing,
      Author author) throws SQLException
  {
    try(Connection connection = getConnection()) {
      PreparedStatement statement = connection.prepareStatement(
          "INSERT INTO Book(isbn, title, "
              + "publishing, author_id) VALUES (?, ?, ?, ?);");
      statement.setString(1, isbn);
      statement.setString(2, title);
      statement.setInt(3, yearOfPublishing);
      statement.setInt(4, author.getId());
      statement.executeUpdate();
      return new Book(isbn, title, yearOfPublishing, author);
    }
  }


  @Override public Book readByISBN(String isbn) throws SQLException
  {
    try(Connection connection = getConnection()) {
      PreparedStatement statement = connection
          .prepareStatement("SELECT * FROM Book JOIN Author ON author_id = id"
              + " WHERE isbn = ?");
      statement.setString(1, isbn);
      ResultSet resultSet = statement.executeQuery();
      if(resultSet.next()) {
        String title = resultSet.getString("title");
        int yearOfPublishing = resultSet.getInt("yearOfPublishing");
        int author_id = resultSet.getInt("author_id");
        String authorName = resultSet.getString("name");
        Author author = new Author(author_id, authorName);
        Book book = new Book(isbn, title, yearOfPublishing, author);
        return book;
      } else {
        return null;
      }
    }
  }

  @Override public List<Book> readByTitle(String searchString)
      throws SQLException
  {
    try(Connection connection = getConnection()) {
      PreparedStatement statement = connection
          .prepareStatement("SELECT * FROM Book JOIN Author ON author_id = id"
              + " WHERE title LIKE ?");
      statement.setString(1, "%" + searchString + "%");
      ResultSet resultSet = statement.executeQuery();
      ArrayList<Book> result = new ArrayList<>();
      while(resultSet.next()) {
        String isbn = resultSet.getString("isbn");
        String title = resultSet.getString("title");
        int yearOfPublishing = resultSet.getInt("yearOfPublishing");
        int author_id = resultSet.getInt("author_id");
        String authorName = resultSet.getString("name");
        Author author = new Author(author_id, authorName);
        Book book = new Book(isbn, title, yearOfPublishing, author);
        result.add(book);
      }
        return result;
    }
  }

  @Override public void update(Book book) throws SQLException
  {
    try(Connection connection = getConnection())
    {
      PreparedStatement statement = connection.prepareStatement(
          "UPDATE Book SET title = ?, yearOfPublishing = ?, author_id = ? " + "WHERE isbn ?");
      statement.setString(1, book.getTitle());
      statement.setInt(2, book.getYearOfPublishing());
      statement.setInt(3, book.getAuthor().getId());
      statement.setString(2, book.getIsbn());
      statement.executeUpdate();
    }
  }

  @Override public void delete(Book book) throws SQLException
  {
    try(Connection connection = getConnection())
    {
      PreparedStatement statement = connection.prepareStatement(
          "DELETE FROM Book WHERE isbn = ?");
      statement.setString(1, book.getIsbn());
      statement.executeUpdate();
    }
  }
}
