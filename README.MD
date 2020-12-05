# Falcon 500 Notifier Bot

- Channels to be messaged for a stock notification are hardcoded
- Notification frequency is also hardcoded

# Usage
- Compile after changes are made
- Create new application in https://discord.com/developers/applications (if you do not already have an application for the bot)
- Create the bot account in the application and copy the token and client id
- Provide the token as argument when running jar
- Add bot to server by going to the link https://discord.com/oauth2/authorize?client_id=INSERT_CLIENT_ID_HERE&scope=bot&permissions=3072 after replacing `INSERT_CLIENT_ID` with your client id
- Type `check talon` as a private message to the bot or in any channel the bot is in to check stock. It will also check periodically automatically.