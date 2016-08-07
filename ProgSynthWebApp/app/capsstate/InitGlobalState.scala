package capsstate

import models.User

object InitGlobalState {
	def test_init() = {
	    val user1 = User("user1@company.com", "User One", "secret1")
        val user2 = User("user2@company.com", "User Two", "secret2")
        val user3 = User("user3@company.com", "User Three", "secret3")
        
        //addDerivation(user1, ExistsTrue)
        //addDerivation(user1, IntSqrt)
        //addDerivation(user1, IntDiv)
        //addDerivation(user1, IntDivStepIntoTest)
        //addDerivation(user1, IntDiv2)
        //addDerivation(user1, MaxSegSum)
        //addDerivation(user1, TTFF7NoBranching)
        //addDerivation(user1, binarySearch2)
        //addDerivation(user1, ArrayMin)
        //addDerivation(user1, Max)
        //addDerivation(user1, TTFF7)
        //addDerivation(user1, DutchNationalFlag)
	    //addDerivation(user1, DutchNationalFlag)
		//addDerivation(user1, TTFF7)
		//addDerivation(user1, binarySearch)
		//addDerivation(user2, allTrue)
		//addDerivation(user2, intDiv)
	}

	//TODO: remove this
	test_init()
}