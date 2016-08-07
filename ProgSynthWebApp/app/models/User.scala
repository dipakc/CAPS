package models

case class User(email: String, name: String, password: String)

object User {

    val users = Seq(
        User("user1@company.com", "User One", "secret1"),
        User("user2@company.com", "User Two", "secret2"),
        User("a@a", "User A", "a"),
        User("guest@guest", "User Guest", "guest"))

    /**
     * Authenticate a User.
     */
    def authenticate(email: String, password: String): Option[User] = {
    	users.find(u => u.email == email && u.password == password)
    }

    /**
     * Retrieve a User from email.
     */
    def findByEmail(email: String): Option[User] = {
        users.find(_.email == email)
    }

/*
  /**
   * Parse a User from a ResultSet
   */
  val simple = {
    get[String]("user.email") ~
    get[String]("user.name") ~
    get[String]("user.password") map {
      case email ~ name ~ password => User(email, name, password)
    }
  }

  /**
   * Retrieve a User from email.
   */
  def findByEmail(email: String): Option[User] = {
    DB.withConnection { implicit connection =>
      SQL("select * from user where email = {email}").on(
        'email -> email).as(User.simple.singleOpt)
    }
  }

  /**
   * Retrieve all users.
   */
  def findAll: Seq[User] = {
    DB.withConnection { implicit connection =>
      SQL("select * from user").as(User.simple *)
    }
  }

  /**
   * Authenticate a User.
   */
  def authenticate(email: String, password: String): Option[User] = {
    DB.withConnection { implicit connection =>
      SQL(
        """
         select * from user where
         email = {email} and password = {password}
        """).on(
          'email -> email,
          'password -> password).as(User.simple.singleOpt)
    }
  }

  /**
   * Create a User.
   */
  def create(user: User): User = {
    DB.withConnection { implicit connection =>
      SQL(
        """
          insert into user values (
            {email}, {name}, {password}
          )
        """).on(
          'email -> user.email,
          'name -> user.name,
          'password -> user.password).executeUpdate()

      user

    }
  }
*/
}

