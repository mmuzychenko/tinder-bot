package com.javarush.telegram;

import com.javarush.telegram.ChatGPTService;
import com.javarush.telegram.DialogMode;
import com.javarush.telegram.MultiSessionTelegramBot;
import com.javarush.telegram.UserInfo;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.objects.*;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import java.util.ArrayList;
import java.util.List;

public class TinderBoltApp extends MultiSessionTelegramBot {
    public static final String TELEGRAM_BOT_NAME = "tinder_adjutant_bot";
    public static final String TELEGRAM_BOT_TOKEN = "7946127126:AAERRH7lw-ZxPL_me4zQ8T5mXDr5F8zgGIs";
    public static final String OPEN_AI_TOKEN = "gpt:YmaS8RBmuYm3tDQQBKzoJFkblB3TwvG9ujguLFP3HbfwU2qc";
    public ChatGPTService gptService = new ChatGPTService(OPEN_AI_TOKEN);
    public DialogMode mode = DialogMode.MAIN;
    public List<String> chat;

    public TinderBoltApp() {
        super(TELEGRAM_BOT_NAME, TELEGRAM_BOT_TOKEN);
    }

    @Override
    public void onUpdateEventReceived(Update update) {

        String message = getMessageText();

        if (message.equalsIgnoreCase("/start")) {
            mode = DialogMode.MAIN;

            showMainMenu(
                    "головне меню бота", "/start",
                    "генерація Tinder-профілю \uD83D\uDE0E", "/profile",
                    "повідомлення для знайомства \uD83E\uDD70", "/opener",
                    "листування від вашого імені \uD83D\uDE08", "/message",
                    "листування із зірками \uD83D\uDD25", "/date",
                    "поставити запитання чату GPT \uD83E\uDDE0", "/gpt"
            );

            String menu = loadMessage("main");
            sendTextMessage(menu);
            sendPhotoMessage("main");

            return;
        }

        if (message.equalsIgnoreCase("/gpt")) {
            mode = DialogMode.GPT;

            String gptMessage = loadMessage("gpt");
            sendTextMessage(gptMessage);
            sendPhotoMessage("gpt");

            return;
        }

        if (mode == DialogMode.GPT) {
            String prompt = loadPrompt("gpt");
            Message msg = sendTextMessage("Почекай...");
            String answer = gptService.sendMessage(prompt, message);
            updateTextMessage(msg, answer);
//            sendTextMessage(answer);

            return;
        }

        if (message.equalsIgnoreCase("/date")) {
            mode = DialogMode.DATE;

            String dateMessage = loadMessage("date");
            sendPhotoMessage("date");

            sendTextButtonsMessage(dateMessage,
                    "Аріана Гранде 🔥", "date_grande",
                    "Марго Роббі 🔥🔥", "date_robbie",
                    "Зендея 🔥🔥🔥", "date_zendaya",
                    "Райан Гослінг 😎", "date_gosling",
                    "Том Харді 😎😎", "date_hardy");

            return;
        }

        if (mode == DialogMode.DATE) {
            String query = getCallbackQueryButtonKey();

            if (query.startsWith("date_")) {
                sendPhotoMessage(query);
                String prompt = loadPrompt(query);
                gptService.setPrompt(prompt);
                return;
            }

            Message msg = sendTextMessage("Почекай...");

            String answer = gptService.addMessage(message);
            updateTextMessage(msg, answer);

            return;
        }

        if (message.equalsIgnoreCase("/message")) {
            mode = DialogMode.MESSAGE;

            String gptMessageHelper = loadMessage("message");
//            sendTextMessage(gptMessageHelper);
            sendPhotoMessage("message");

            sendTextButtonsMessage(gptMessageHelper,
                    "Наступне повідомлення", "message_next",
                    "Запросити на побачення", "message_date");

            chat = new ArrayList<>();

            return;
        }

        if (mode == DialogMode.MESSAGE) {
            String query = getCallbackQueryButtonKey();

            if (query.startsWith("message_")) {
                String prompt = loadPrompt(query);
                String history = String.join("/n/n", chat);

                Message msg = sendTextMessage("Почекай...");

                String answer = gptService.sendMessage(prompt, history);
                updateTextMessage(msg, answer);
//                sendTextMessage(answer);
            }

            chat.add(message);
            return;
        }

//        sendTextMessage("_" + message + "_");
//
//
//        sendTextButtonsMessage("Button message", "START", "start", "STOP", "stop");


    }

    public static void main(String[] args) throws TelegramApiException {
        TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
        telegramBotsApi.registerBot(new TinderBoltApp());
    }
}
