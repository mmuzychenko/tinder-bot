package com.javarush.telegram;

import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.objects.*;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import java.util.ArrayList;
import java.util.List;

public class TinderBoltApp extends MultiSessionTelegramBot {
    public static final String TELEGRAM_BOT_NAME = "tinder_adjutant_bot";
    public static final String TELEGRAM_BOT_TOKEN = "7946127126:AAERRH7lw-ZxPL_me4zQ8T5mXDr5F8zgGIs";
    public static final String OPEN_AI_TOKEN = "";
    public ChatGPTService gptService = new ChatGPTService(OPEN_AI_TOKEN);
    public DialogMode mode = DialogMode.MAIN;
    private List<String> chat;
    private UserInfo myInfo;
    private UserInfo personInfo;
    private int questionNumber;

    public TinderBoltApp() {
        super(TELEGRAM_BOT_NAME, TELEGRAM_BOT_TOKEN);
    }

    @Override
    public void onUpdateEventReceived(Update update) {

        String message = getMessageText();

        switch (message) {
            case "/start" -> {
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
            case "/gpt" -> {
                mode = DialogMode.GPT;

                String gptMessage = loadMessage("gpt");
                sendTextMessage(gptMessage);
                sendPhotoMessage("gpt");
                return;
            }
            case "/date" -> {
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
            case "/message" -> {
                mode = DialogMode.MESSAGE;

                String gptMessageHelper = loadMessage("message");
                sendPhotoMessage("message");

                sendTextButtonsMessage(gptMessageHelper,
                        "Наступне повідомлення", "message_next",
                        "Запросити на побачення", "message_date");

                chat = new ArrayList<>();
                return;
            }
            case "/profile" -> {
                mode = DialogMode.PROFILE;

                sendPhotoMessage("profile");
                String profileMessage = loadMessage("profile");
                sendTextMessage(profileMessage);

                myInfo = new UserInfo();
                questionNumber = 1;
                sendTextMessage("Введіть ім'я?");

                return;
            }
            case "/opener" -> {
                mode = DialogMode.OPENER;

                sendPhotoMessage("opener");
                String profileMessage = loadMessage("opener");
                sendTextMessage(profileMessage);

                personInfo = new UserInfo();
                questionNumber = 1;
                sendTextMessage("Введіть ім'я?");

                return;
            }
        }

        switch (mode) {
            case GPT -> {
                String prompt = loadPrompt("gpt");
                Message msg = sendTextMessage("ChatGPT думає.....");
                String answer = gptService.sendMessage(prompt, message);
                updateTextMessage(msg, answer);
            }
            case DATE -> {
                String query = getCallbackQueryButtonKey();

                if (query.startsWith("date_")) {
                    sendPhotoMessage(query);
                    String prompt = loadPrompt(query);
                    gptService.setPrompt(prompt);
                    return;
                }

                Message msg = sendTextMessage("ChatGPT думає.....");
                String answer = gptService.addMessage(message);
                updateTextMessage(msg, answer);
            }
            case MESSAGE -> {
                String query = getCallbackQueryButtonKey();

                if (query.startsWith("message_")) {
                    String prompt = loadPrompt(query);
                    String history = String.join("/n/n", chat);

                    Message msg = sendTextMessage("ChatGPT думає.....");

                    String answer = gptService.sendMessage(prompt, history);
                    updateTextMessage(msg, answer);
                }

                chat.add(message);
            }
            case PROFILE -> {
                if (questionNumber <= 6) {
                    askQuestion(message, myInfo, "profile");
                }
            }
            case OPENER -> {
                if (questionNumber <= 6) {
                    askQuestion(message, personInfo, "opener");
                }
            }
        }

    }

    private void askQuestion(String message, UserInfo user, String profileName) {
        switch (questionNumber) {
            case 1 -> {
                user.setName(message);
                questionNumber = 2;
                sendTextMessage("Введіть вік?");
            }
            case 2 -> {
                user.setAge(message);
                questionNumber = 3;
                sendTextMessage("Введіть місто?");
            }
            case 3 -> {
                user.setCity(message);
                questionNumber = 4;
                sendTextMessage("Введіть професію?");
            }
            case 4 -> {
                user.setOccupation(message);
                questionNumber = 5;
                sendTextMessage("Введіть хоббі?");
            }
            case 5 -> {
                user.setHobby(message);
                questionNumber = 6;
                sendTextMessage("Введіть цілі для знайомства?");
            }
            case 6 -> {
                user.setGoals(message);
                String prompt = loadPrompt(profileName);
                Message msg = sendTextMessage("ChatGPT думає.....");
                String answer = gptService.sendMessage(prompt, user.toString());
                updateTextMessage(msg, answer);
            }
        }
    }

    public static void main(String[] args) throws TelegramApiException {
        TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
        telegramBotsApi.registerBot(new TinderBoltApp());
    }
}
