# <img alt='SecretManager' src='https://github.com/miguelromeral/SecretManager/blob/master/app/src/main/ic_launcher_sm_v2-web.png' height="35" width="auto" /> How to use Secret Manager: FAQs

<a href='https://play.google.com/store/apps/details?id=es.miguelromeral.secretmanager&pcampaignid=MKT-Other-global-all-co-prtnr-py-PartBadge-Mar2515-1'><img alt='Get it on Google Play' src='https://play.google.com/intl/en_us/badges/images/generic/en_badge_web_generic.png' height="70" width="auto" /></a>

## Q1: I can't see the app in Google Play or it doesn't allow me to download the app.

Secret Manager is available for Android devices which have Android 7.1 (Nougat) or better. You can check out your current Android version in Settings App > About Device > Operative System Version

If you type "Secret Manager" or "MiguelRomeral" on Google Play and it doesn't show you the application to be downloaded, then your device is not able to support the application and can't be neither downloaded nor installed on that device.

## Q2: Why do I need to allow the app to access my files?

The app needs the user to allow _**Storage Permission**_ in order to export the secrets into a file in your Android Device, exactly in the next folder:

```Internal Storage\Android\data\es.miguelromeral.secretmanager\files\Documents\ ```

The file generated after clicking, in the Secrets fragment, the *Export Secrets* option is called **secret_mmanager.csv** and can be readable by any text reader application such Notepad or Excel.

**Warning: If you want to change your Android Device and you would like to keep your secrets in the Secret Manager app in your new device, you should export first the secrets stored in your old device. Otherwise, your data will not be restored by its own.**

## Q3: The secrets are not exported when I clicked *Import Secrets* button

When you click that button, the app looks for a file named **secret_manager.csv** in the same directory the secrets are exported (indicated in Q2). Please, make sure the file you are trying to import is called with the exactly same name as specified moments before and with the next format:

```
id,"time","content","alias"
1,"1579517268000,"asdfghjkl=","First Secret"
2,"1579517268200,"qwertyuiop=","Second Secret"
```

If the file doesn't have a CSV structure like the example given, the app will not be able to read your data.

## Q4: Some secrets are imported but not all of them.

The importation of the secrets takes into account the unique ID to store it on the local database. If the secret you're trying to import has the same ID as another one already stored on the database, the app will miss it.

To fix this issue, you can change manually the ID editing the file you want to import and replacing the ID of the secret which is not being imported by another ID.

The reason of this issue is to avoid importing the same secret when it could be already on the database. Sorry for the inconvenience.