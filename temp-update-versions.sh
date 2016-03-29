#!/bin/sh

# Checks if return code is 0, otherwise stops the execution
function checkRC() {
    rc=$?
    if [[ $rc != 0 ]]; then
        echo "[ERROR] Something went wrong in STEP $STEP. Please fix the issue and run the script again. Return code is: $rc"
        echo ""
        printRevertSteps
        exit $rc
    fi
}

# Checks variable is not empty.
function checkNotEmpty() {
    if [[ -z $1 ]]; then
        echo "[ERROR] Variable is empty. Please fix your environment issues and run script again."
        echo ""
        printRevertSteps
        exit 1
    fi

}

# Waits until user press enter
function step() {
    STEP=$1
    echo ""
    echo "    [STEP $STEP]"
}

# Print manual steps to revert pending changes
function printRevertSteps() {

    if [[ ! -z $MAIN_CHANGELIST_NUMBER ]]; then
        echo ""
        echo "     ______________________________________________________________________________________"
        echo "    |                                                                                      |"
        echo "    |     If P4 changelist was created but not submitted yet, please revert it!!!          |"
        echo "    |     Revert steps are shown below:                                                    |"
        echo "    |______________________________________________________________________________________|"
        echo ""
        echo "        1. Look at all pending changes:       p4 changes -c $CLIENT_NAME -s pending"
        echo "        2. Revert pending changelist:         p4 revert -c $MAIN_CHANGELIST_NUMBER //..."
        echo "        3. Delete pending changelist:         p4 change -d $MAIN_CHANGELIST_NUMBER"
        echo ""
    fi
}

if [ "$1" == "submit" ]; then
    MODE=submit
    echo ""
    echo "                   __              _ __                         __    "
    echo "       _______  __/ /_  ____ ___  (_) /_   ____ ___  ____  ____/ /__  "
    echo "      / ___/ / / / __ \/ __ \`__ \/ / __/  / __ \`__ \/ __ \/ __  / _ \ "
    echo "     (__  ) /_/ / /_/ / / / / / / / /_   / / / / / / /_/ / /_/ /  __/ "
    echo "    /____/\__,_/_.___/_/ /_/ /_/_/\__/  /_/ /_/ /_/\____/\__,_/\___/  "
    echo ""
else
    MODE=normal
fi


step 1
echo "You will be prompted to enter PASSWORD for current perforce user to login to P4."
echo ""
echo "      ===========================================================  "
echo "    ||         _ ___ .  __          _   _  _      .  _   _  _    ||"
echo "    ||    /\  |   |  | |  | |\ |   |_| |_ | | | | | |_| |_ | \   ||"
echo "    ||   /--\ |_  |  | |__| | \|   | \ |_ |_| |_| | | \ |_ |_/   ||"
echo "    ||                                      '                    ||"
echo "      ===========================================================  "
echo ""
p4 login
checkRC

echo "Checking variables..."
CLIENT_ROOT=$(p4 info | grep 'Client root:' | cut -d ' ' -f 3-)
checkRC
echo "Client root: $CLIENT_ROOT"
checkNotEmpty $CLIENT_ROOT

CLIENT_NAME=$(p4 info | grep 'Client name:' | cut -d ' ' -f 3-)
checkRC
echo "Client name: $CLIENT_NAME"
checkNotEmpty $CLIENT_NAME
echo ""


step 2
CURRENT_VERSION=$(head /home/builder/work/depot/product/Commons/main/pom.xml | grep SNAPSHOT | sed -r 's/.+([0-9]+.[0-9]+).+/\1/')
checkNotEmpty $CURRENT_VERSION

echo "You will be prompted to enter Maven SNAPSHOT version that will be used for next release."
echo ""
echo "      ===========================================================  "
echo "    ||         _ ___ .  __          _   _  _      .  _   _  _    ||"
echo "    ||    /\  |   |  | |  | |\ |   |_| |_ | | | | | |_| |_ | \   ||"
echo "    ||   /--\ |_  |  | |__| | \|   | \ |_ |_| |_| | | \ |_ |_/   ||"
echo "    ||                                      '                    ||"
echo "      ===========================================================  "
echo ""
echo "Please enter NEW maven snapshot version of project."
read -p "Current release version is $CURRENT_VERSION, so please INCREASE it. If you enter X.Y, then version in pom.xml will be set to: 'X.Y-SNAPSHOT': " NEW_VERSION
CORRECT_VALUE=false
while [ "$CORRECT_VALUE" = false ]; do
    if [ $(echo $NEW_VERSION | egrep -c "^[0-9]+\.[0-9]+$") -ne 0 ]; then
        CORRECT_VALUE=true
    else
        read -p "You have entered incorrect value, version should contain only digits and one dot (i.e.: 4.2 ): " NEW_VERSION
    fi
done


step 3
echo "Creating perforce changelist to increase Maven version in TCP&TPG main branch from $CURRENT_VERSION-SNAPSHOT to $NEW_VERSION-SNAPSHOT"

MAIN_CHANGELIST_NUMBER=$(p4 change -o | \
    sed "s/<enter description here>/TCP\&TPG maven version stepped to $NEW_VERSION-SNAPSHOT/" | \
    sed '/^#/d' | \
    sed '/^$/d' | \
    p4 change -i | \
    cut -d ' ' -f 2)

echo ""
echo "Setting JAVA_HOME variable..."
export JAVA_HOME=/apphome/java/jdk8
$JAVA_HOME/bin/java -version
checkRC
echo ""

echo "Increase Maven version in TCP main branch from $CURRENT_VERSION-SNAPSHOT to $NEW_VERSION-SNAPSHOT..."
$CLIENT_ROOT/depot/tools/groovy-1.0/bin/groovy "$CLIENT_ROOT/depot/tools/perforce-util/changeVersion.groovy" "$CLIENT_ROOT/depot/product/Commons/main" $CURRENT_VERSION-SNAPSHOT $NEW_VERSION-SNAPSHOT $MAIN_CHANGELIST_NUMBER
checkRC
echo ""

echo "Increase Maven version in TPG main branch from $CURRENT_VERSION-SNAPSHOT to $NEW_VERSION-SNAPSHOT..."
$CLIENT_ROOT/depot/tools/groovy-1.0/bin/groovy "$CLIENT_ROOT/depot/tools/perforce-util/changeVersion.groovy" "$CLIENT_ROOT/depot/product/PaymentGateway/main" $CURRENT_VERSION-SNAPSHOT $NEW_VERSION-SNAPSHOT $MAIN_CHANGELIST_NUMBER
checkRC
echo ""


step 4
if [ "$MODE" == "submit" ]; then
    echo "Submitting increased maven versions for Commons and PaymentGateway..."
    echo ""
    p4 submit -c $MAIN_CHANGELIST_NUMBER
    checkRC
    echo ""
else
    echo "Skipping submit step for perforce changelist $MAIN_CHANGELIST_NUMBER since the script is not running in submit mode...."
fi

echo ""
echo ""
echo "      ====================================  "
echo "    ||    __ .      .  __        _   _    ||"
echo "    ||   |__ | |\ | | |__  |__| |_  | \   ||"
echo "    ||   |   | | \| |  __| |  | |_  |_/   ||"
echo "    ||                                    ||"
echo "      ====================================  "
echo ""
echo "Do not forget to change Build configuration name/description in TeamCity for stable branch once version change is merged to it!"

if [ "$MODE" != "submit" ]; then
    printRevertSteps
fi