package progsynth.provers

object PSProverConfigWS {
  println("Welcome to the Scala worksheet")       //> Welcome to the Scala worksheet
  /* Config Hierarchy.
  * ProverConf > Z3Config
  * 			  > Why3Config > Why3AltErgoConfig
  * */
  import PSProverConfigInit._
  //set overall timeout
  proverConfig.setParam("timeout", "10000")
  proverConfig.getParam("timeout")                //> res0: Option[String] = Some(10000)
  z3Config.getParam("timeout")                    //> res1: Option[String] = Some(10000)
  why3Config.getParam("timeout")                  //> res2: Option[String] = Some(10000)
  why3AltErgoConfig.getParam("timeout")           //> res3: Option[String] = Some(10000)
  //set timout of prover and why3
  proverConfig.setParam("timeout", "5000")
  why3Config.setParam("timeout", "8000")
  /////////////
  proverConfig.getParam("timeout")                //> res4: Option[String] = Some(5000)
  z3Config.getParam("timeout")                    //> res5: Option[String] = Some(5000)
  why3Config.getParam("timeout")                  //> res6: Option[String] = Some(8000)
  why3AltErgoConfig.getParam("timeout")           //> res7: Option[String] = Some(8000)
  //////////////
  val z3Prover = new PSZ3Prover()                 //> z3Prover  : progsynth.provers.PSZ3Prover = progsynth.provers.PSZ3Prover@1415
                                                  //| de6
  //get timeout for the instantiated z3Prover
  z3Prover.config.getParam("timeout")             //> res8: Option[String] = Some(5000)
  //set timeout of the instantiated prover
  z3Prover.config.setParam("timeout", "7000")
  z3Prover.config.getParam("timeout")             //> res9: Option[String] = Some(7000)
  //z3Config timeout still at the old value.
  z3Config.getParam("timeout")                    //> res10: Option[String] = Some(5000)
}