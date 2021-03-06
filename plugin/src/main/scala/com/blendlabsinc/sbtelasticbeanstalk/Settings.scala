package com.blendlabsinc.sbtelasticbeanstalk

import com.blendlabsinc.sbtelasticbeanstalk.core.AWS
import com.blendlabsinc.sbtelasticbeanstalk.ElasticBeanstalkKeys._
import com.github.play2war.plugin.Play2WarKeys
import sbt.{ Setting, Hash }
import sbt.Keys.{ baseDirectory, commands }

trait ElasticBeanstalkSettings {
  this: ElasticBeanstalkCommands with ElasticBeanstalkAPICommands =>

  val sessionId = new java.math.BigInteger(130, new java.security.SecureRandom()).toString(32) // HACK TODO
  lazy val elasticBeanstalkSettings = Seq[Setting[_]](
    ebAppBundle <<= Play2WarKeys.war,
    ebEnvironmentNameSuffix := { (name) =>
      val maxLen = 23
      val uniq = sessionId
      if (name.length > (23 - 7)) throw new Exception("environment name too long: " + name)
      name + "-" + System.getenv("USER").take(3) + uniq.take(3)
    },
    ebDeploy <<= ebDeployTask,
    ebQuickUpdate <<= ebQuickUpdateTask,
    ebSetUpEnvForAppVersion <<= ebSetUpEnvForAppVersionTask,
    ebWait <<= ebWaitForEnvironmentsTask,
    ebParentEnvironments <<= ebParentEnvironmentsTask,
    ebTargetEnvironments <<= ebTargetEnvironmentsTask,
    ebExistingEnvironments <<= ebExistingEnvironmentsTask,
    ebCleanEnvironments <<= ebCleanEnvironmentsTask,
    ebCleanAppVersions <<= ebCleanAppVersionsTask,
    ebUploadSourceBundle <<= ebUploadSourceBundleTask,
    ebConfigPull <<= ebConfigPullTask,
    ebConfigPush <<= ebConfigPushTask,
    ebConfigDirectory <<= baseDirectory / "eb-deploy",
    ebClient <<= (ebRegion) map { (region) => AWS.elasticBeanstalkClient(region) },
    ec2Client <<= (ebRegion) map { (region) => AWS.ec2Client(region) },
    ebApiDescribeApplications <<= ebApiDescribeApplicationsTask,
    ebApiDescribeEnvironments <<= ebApiDescribeEnvironmentsTask,
    ebNotify := { (deployment, env, msg) => },
    commands ++= Seq(ebApiRestartAppServer),
    ebRequireJava6 := true
  )
}
