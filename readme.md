# MesaTraining

This repository contains the primary Java-based frontends for the web applets that will link to the database. Ideally, in order to properly collaborate, you will need to use a Java-based IDE such as Eclipse or Netbeans. The steps below detail how to set up Eclipse, such that it correctly interfaces with this repo.

1. Visit https://www.eclipse.org/downloads/packages/eclipse-ide-java-developers/lunasr1a and acquire the correct copy for your system.


2. Unpack it, and launch the Application.
2a. If you are confronted with an error that Eclipse could not locate javaw.exe: Visit http://www.oracle.com/technetwork/java/javase/downloads/jre8-downloads-2133155.html to download JRE 8, and install it. 


3. In order to access Git, first, navigate to Window > Open Perspective > Other. Locate the entry labeled "Git", and hit "Okay".
3a. If you do not see Git in the list, navigate to Help > Install New Software. For "Available Sites" select "--All Available Sites--", and then search for the term "Git". 

NOTE: It may take some time for Eclipse to populate the results. Locate "Eclipse Git Team Provider", check all entrys within it, and hit "Install/Finish".

4. Once you have opened the Git perspective, a new tab on the left will appear with the database icon. Open it. The tab labeled "Git Repositories" should now be popped open. Select the option labeled "Clone a Git Repository". 
URI: https://github.com/MesaLabs1/MesaTraining.git
A few fields will fill in automatically. 
Now, for Authentication, enter the login information for your GitHub account, provided you've been added as a collaborator. 
Optional: Check "Store in Secure store". Hit Next.


5. Once it is done, make sure "Master" is checked. Hit Next.

6. Check "Clone Submodules", "Import all existing projects after clone finishes, and "Add Projects to working sets". Hit finish.

7. You now have access to the Git repository from Eclipse. Remember that you can toggle between both perpectives via the top 	 right buttons labeled "Java" and "Git". Other buttons will appear here as you open more perspectives.


Important Note: This software implements Derby and DTP (Database Tools Platform) by Java. In order to install these into your target environment, we have already added the Derby libs into the git system, and added them to the build path. However you must manually intall DTP, which is included in the git download as a Zip. To install this, locate the file labeled dtp-sdk_X.XX.X.zip, and extract it to YOUR eclipse directory, and overwrite any files it asks for. The plugin is now installed. All compiler errors and db errors should now be resolved.

How to Add/Remove/Update the Repo with your work

1. All modifications you make can be applied by right clicking the "MesaTraining" entry in Git, and hitting "Commit", and then after posting a commit message, selecting "Commit and Push". This will sync the project with the upstream.

If you get an error saying "Non-fast forward" you have most likely pushed an empty commit.
