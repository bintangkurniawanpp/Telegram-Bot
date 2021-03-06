package utils;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import twitter4j.JSONObject;

import java.io.File;
import java.io.IOException;

public class Bot extends TelegramLongPollingBot {
    private String channelChatId = "@PLN_NewsFeed";

    @Override
    public void onUpdateReceived(Update update) {
        SendMessage sendMessage = new SendMessage().setChatId(update.getMessage().getChatId());
        MessageHandler message = new MessageHandler();
        String chatId = String.valueOf(update.getMessage().getChatId());
        String text = update.getMessage().getText();
        String url;
        if (update.getMessage().getPhoto() != null) {
            String fileId = update.getMessage().getPhoto().get(0).getFileId();
            url = String.format("https://api.telegram.org/bot%s/getFile?file_id=%s", getBotToken(), fileId);
            try {
                JSONObject json = Helper.readJsonFromUrl(url);
                json = (JSONObject) json.get("result");
                String filePath = json.getString("file_path");
                url = String.format("https://api.telegram.org/file/bot%s/%s", getBotToken(), filePath);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (update.getMessage().getVideo() != null) {
            String fileId = update.getMessage().getVideo().getFileId();
            url = String.format("https://api.telegram.org/bot%s/getFile?file_id=%s", getBotToken(), fileId);
            try {
                JSONObject json = Helper.readJsonFromUrl(url);
                json = (JSONObject) json.get("result");
                String filePath = json.getString("file_path");
                url = String.format("https://api.telegram.org/file/bot%s/%s", getBotToken(), filePath);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        sendMessage.setText(message.readMessage(chatId, text));

        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getBotUsername() {
        return null;
    }

    @Override
    public String getBotToken() {
        return "1015235115:AAHrIAwEI5H8w4JKjWrasPJtUSeReeyR87M";
//        return "1208946764:AAGXHSYwDRD1h9HHb6ZKLnQChzbNnB4zlr8";
    }

    public void broadcast(File file, String messages) throws TelegramApiException {
        if (file != null) {

            SendDocument document = new SendDocument().setChatId(channelChatId)
                    .setDocument(file)
                    .setCaption(messages);
            execute(document);


        } else {
            SendMessage msg = new SendMessage().setChatId(channelChatId)
                    .setText(messages);
            execute(msg);
        }
    }

    public void broadcast(String messages) throws TelegramApiException {
        SendMessage msg = new SendMessage().setChatId(channelChatId)
                .setText(messages);
        execute(msg);
    }

}
