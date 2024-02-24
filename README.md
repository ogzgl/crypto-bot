Steps to run the program:

1. use "./init-db.sh docker clean" to create the database in docker environment
2. Install ngrok
3. use "ngrok http localhost:8080"
4. Assuming you have created a bot in Telegram, put your token to application.yaml file
5. and use this url:
   1. https://api.telegram.org/{YOUR_BOT_TOKEN}/setWebhook?url={NGROK_ADDRESS_FROM_CONSOLE}]/telegram
6. After completing the above steps and sending priceCheck {threshold} on the chat with bot you will be able to see the messages coming from the bot to telegram with the threshold you've set
7. You can use the priceCheckRestart {threshold} to set the initial time to now