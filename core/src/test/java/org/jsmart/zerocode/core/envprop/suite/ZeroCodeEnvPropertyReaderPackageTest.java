package org.jsmart.zerocode.core.envprop.suite;

import org.jsmart.zerocode.core.domain.EnvProperty;
import org.jsmart.zerocode.core.domain.TargetEnv;
import org.jsmart.zerocode.core.domain.TestPackageRoot;
import org.junit.runner.RunWith;

// see "ENV_NAME=ci" in .bash_profile file or pass via 'mvn -DENV_NAME=ci'.
// If not found, then defaults to "app_config.properties"

@EnvProperty("_${ENV_NAME}")
@TargetEnv("package_properties/app_config.properties")
@TestPackageRoot("14_env_prop")
@RunWith(NewPortTestZeroCodeUnitRunner.class)
//@RunWith(ZeroCodePackageRunner.class)
public class ZeroCodeEnvPropertyReaderPackageTest {

}
