package com.blendlabsinc.sbtelasticbeanstalk.core

import com.amazonaws.auth.AWSCredentials
import com.amazonaws.services.elasticbeanstalk.model._
import com.amazonaws.services.elasticbeanstalk.AWSElasticBeanstalkClient
import scala.collection.JavaConverters._

class Deployer(
  appName: String,
  envName: String,
  awsCredentials: AWSCredentials,
  region: String
) {
  val eb = new AWSElasticBeanstalkClient(awsCredentials)
  eb.setEndpoint("https://elasticbeanstalk." + region + ".amazonaws.com")

  def deploy(versionLabel: String, bundleS3Location: S3Location, envVars: Map[String,String]): UpdateEnvironmentResult = {
    val versionDesc = createAppVersion(versionLabel, bundleS3Location)
    updateEnvironmentVersionLabel(versionDesc, envVars)
  }

  private def createAppVersion(versionLabel: String, bundleS3Location: S3Location): ApplicationVersionDescription = {
    eb.createApplicationVersion(
      new CreateApplicationVersionRequest()
        .withApplicationName(appName)
        .withVersionLabel(versionLabel)
        .withSourceBundle(bundleS3Location)
        .withDescription("Deployed by " + System.getenv("USER"))
    ).getApplicationVersion
  }

  private def updateEnvironmentVersionLabel(
    newVersion: ApplicationVersionDescription,
    envVars: Map[String,String]
  ): UpdateEnvironmentResult = {
    eb.updateEnvironment(
      new UpdateEnvironmentRequest()
        .withEnvironmentName(envName)
        .withVersionLabel(newVersion.getVersionLabel)
        .withOptionSettings(
          envVars.map { case (k, v) =>
            new ConfigurationOptionSetting("aws:elasticbeanstalk:application:environment", k, v)
          }.asJavaCollection
        )
    )
  }
}
