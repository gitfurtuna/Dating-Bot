package com.datingbot.telegram;

import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.objects.*;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import java.util.ArrayList;

public class DatingBotApp extends MultiSessionTelegramBot {
    public static final String TELEGRAM_BOT_NAME = " "; //TODO: добавь имя бота в кавычках
    public static final String TELEGRAM_BOT_TOKEN = " "; //TODO: добавь токен бота в кавычках
    public static final String OPEN_AI_TOKEN = " "; //TODO: добавь токен ChatGPT в кавычках

    private ChatGPTService chatGPT = new ChatGPTService(OPEN_AI_TOKEN);

    private DialogMode currentMode = null;

    private ArrayList<String> list = new ArrayList<>();

    private UserInfo me;

    private UserInfo he;

    private int questionCount;
    public DatingBotApp() {
        super(TELEGRAM_BOT_NAME, TELEGRAM_BOT_TOKEN);
    }

    @Override
    public void onUpdateEventReceived(Update update) {
        String message = getMessageText();

        if (message.equals("/start")) {
            currentMode = DialogMode.MAIN;
            sendPhotoMessage("main");
            String text = loadMessage("main");
            sendTextMessage(text);

            showMainMenu("главное меню бота","/start",
                    "генерация Tinder-профля \uD83D\uDE0E", "/profile",
                    "сообщение для знакомства \uD83E\uDD70","/opener",
                    "переписка от вашего имени \uD83D\uDE08","/message",
                    "переписка со звездами \uD83D\uDD25","/date",
                    "задать вопрос чату GPT \uD83E\uDDE0","/gpt"

                    );
            return;
        }


        if(message.equals("/gpt")) {
            currentMode = DialogMode.GPT;
            sendPhotoMessage("gpt");
            String text = loadMessage("gpt");
            sendTextMessage(text);
            return;
        }

        if (currentMode == DialogMode.GPT && !isMessageCommand()) {
            String promt = loadPrompt("gpt");

            Message msg = sendTextMessage("Подождите пару секунд - ChatGPT думает...");
            String answer = chatGPT.sendMessage(promt, message);
            updateTextMessage(msg, answer);
            return;
        }


        if (message.equals("/date")) {
            currentMode = DialogMode.DATE;
            sendPhotoMessage("date");
            String text = loadMessage("date");
            sendTextButtonsMessage(text, "Ариана Гранде","date_grande",
                    "Марго Робби","date_robbie",

                    "Зендея","date_zendaya",

                    "Райн Гослинг","date_gosling",

                    "Том Харди","date_hardy"
                    );
            return;
        }

        if (currentMode == DialogMode.DATE && !isMessageCommand()) {
            String query = getCallbackQueryButtonKey();
            if (query.startsWith("date_")) {
                sendPhotoMessage(query);
                sendTextMessage("Отличный выбор! \nТвоя задача пригласить девушку/парня на свидание ❤️\0 ️за 5 сообщений.");

                String prompt = loadPrompt(query);
                chatGPT.setPrompt(prompt);
                return;
            }

            Message msg = sendTextMessage("Подождите, девушка набирает текст... ");
            String answer = chatGPT.addMessage(message);
            updateTextMessage(msg, message);
            return;

        }


        if (message.equals("/message")) {
            currentMode = DialogMode.MESSAGE;
            sendPhotoMessage("message");
            sendTextButtonsMessage("Пришлите в чат вашу переписку",
                    "Следующее сообщение","message_next",
                    "Пригласить на свидание","message_date"
                    );
            return;
        }

        if (currentMode == DialogMode.MESSAGE && !isMessageCommand()) {
            String query = getCallbackQueryButtonKey();
            if (query.startsWith("message_")) {
                String prompt = loadPrompt(query);
                String userChatHistory = String.join("\n\n", list);

                Message msg = sendTextMessage("Подождите пару секунд - ChatGPT думает...");
                String answer = chatGPT.sendMessage(prompt, message);
                updateTextMessage(msg, answer);
                return;
            }
            list.add(message);
            return;
        }


        if(message.equals("/profile")) {
            currentMode = DialogMode.PROFILE;
            sendPhotoMessage("profile");

            me = new UserInfo();
            questionCount = 1;
            sendTextMessage("Сколько вам лет?");
            return;
        }


        if (currentMode == DialogMode.PROFILE && !isMessageCommand()) {
            switch (questionCount) {
                case 1:
                    me.age = message;
                    questionCount = 2;
                    sendTextMessage("Кем вы работаете?");
                    return;
                case 2:
                    me.occupation = message;
                    questionCount = 3;
                    sendTextMessage("У вас есть хобби?");
                    return;
                case 3:
                    me.hobby = message;
                    questionCount = 4;
                    sendTextMessage("Что вам НЕ нравится в людях?");
                    return;
                case 4:
                    me.annoys = message;
                    questionCount = 5;
                    sendTextMessage("Цель знакомства?");
                    return;
                case 5:
                    me.goals = message;

                    String aboutMyself = me.toString();
                    String prompt = loadPrompt("profile");

                    Message msg = sendTextMessage("Подождите пару секунд - ChatGPT \uD83E\uDDE0 думает...");
                    String answer = chatGPT.sendMessage(prompt, aboutMyself);
                    updateTextMessage(msg, answer);
                    return;
            }

            return;
        }


        if(message.equals("/opener")) {
            currentMode = DialogMode.OPENER;
            sendPhotoMessage("opener");

            he = new UserInfo();
            questionCount = 1;
            sendTextMessage("Имя парня?");
            return;
        }

        if (currentMode == DialogMode.OPENER && !isMessageCommand()) {
            switch (questionCount) {
                case 1:
                    he.name = message;
                    questionCount = 2;
                    sendTextMessage("Сколько ему лет?");
                    return;
                case 2:
                    he.age = message;
                    questionCount = 3;
                    sendTextMessage("Есть ли у него хобби и какие?");
                    return;
                case 3:
                    he.hobby = message;
                    questionCount = 4;
                    sendTextMessage("Кем он работает?");
                    return;
                case 4:
                    he.occupation = message;
                    questionCount = 5;
                    sendTextMessage("Цель знакомства?");
                    return;
                case 5:
                    he.goals = message;

                    String aboutFriend = message;
                    String prompt = loadPrompt("profile");

                    Message msg = sendTextMessage("Подождите пару секунд - ChatGPT \uD83E\uDDE0 думает...");
                    String answer = chatGPT.sendMessage(prompt, aboutFriend);
                    updateTextMessage(msg, answer);
                    return;
            }
            return;
        }



        sendTextMessage("*Привет!*");
        sendTextMessage("_Привет!_");


        sendTextMessage("Вы написали " + message);

        sendTextButtonsMessage("Выберите режим работы:", "Старт","start","Стоп","stop");


    }

    public static void main(String[] args) throws TelegramApiException {
        TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
        telegramBotsApi.registerBot(new DatingBotApp());
    }
}
