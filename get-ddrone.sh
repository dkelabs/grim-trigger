#!/bin/bash

# For now, I know user home
user_home="/home/pi"
echo "Go to ${user_home} dir"
echo ""
cd $user_home

# Put the alfa in monitor mode
sudo airmon-ng check kill \
&& sudo airmon-ng start wlan1

# sets the ssh keys to reference for git
export GIT_SSH="${user_home}/git-ssh.sh"
export GIT_SSH_KEY="${user_home}/.ssh/id_rsa"
# Remove the current ~/ddrone-pi dir
echo "Remove the current ${user_home}/ddrone-pi dir"
sudo rm -rf "${user_home}/ddrone-pi"

# TODO: Check if dke-labs-grim-trigger already exists
#       If it does, update it
dke_git_dir="${user_home}/dke-labs-grim-trigger"
ddrone_repo="git@github.com:asmattic/dke-labs-grim-trigger.git" 
if [ -d "$dke_git_dir" ]
then
    echo "${dke_labs_git_dir} exists"
    # echo "Here are the branches"
    cd $dke_git_dir \
    # && git branch -a
    # echo "Type the one you want and hit [ENTER]"
    # read git_branch_to_pull
    git_branch_to_pull="master"
    echo "You chose ${git_branch_to_pull}, about to pull"
    git checkout ${git_branch_to_pull} \
    && git pull origin ${git_branch_to_pull}
else
    echo "Get the whole GitHub repo"
    echo "${ddrone_repo}"
    echo ""
    git clone $ddrone_repo
fi

echo "Copy just the ddrone-pi dir to ${user_home}"
cp -r ${user_home}/dke-labs-grim-trigger/ddrone-pi ${user_home}/ \
&& cd "${user_home}/ddrone-pi"

# echo "Real wifi password to append the the wordlist [ENTER]"
# read real_wifi_pass

# cat <<EndOfText >> $ddrone_dir/wordlists/wordlist.txt
# ${real_wifi_pass}
# EndOfText
# echo "Placing wifi password"
# echo -e "\n${real_wifi_pass}" | sudo tee -a ${user_home}/ddrone-pi/wordlists/wordlist.txt

# Run sudo ./setup-bluetooth.sh
echo "About to run setup-bluetooth.sh"
cd ${user_home}/ddrone-pi \
&& sudo ./setup-bluetooth.sh

echo -e "\nDone"