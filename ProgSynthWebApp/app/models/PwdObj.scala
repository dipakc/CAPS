package models

import org.mindrot.jbcrypt.BCrypt

object PwdObj {
    def createPassword(clearString: String) = {
        if (clearString == "") {
            throw new RuntimeException("empty.password")
        }

        BCrypt.hashpw(clearString, BCrypt.gensalt());
    }

    def checkPassword(candidate: String, encryptedPassword: String): Boolean = {
        BCrypt.checkpw(candidate, encryptedPassword);
    }
}