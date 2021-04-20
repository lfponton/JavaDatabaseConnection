import java.sql.SQLException;
import java.util.List;

public interface AuthorDAO
{
  Author create(int id, String name)
      throws SQLException;
  Author readById(int id) throws SQLException;
  List<Author> readByName(String searchString) throws SQLException;
  void update(Author author) throws SQLException;
  void delete(Author author) throws SQLException;
}
