package com.wyre.trade.helper;

public class URLHelper {
    public static final String base = "https://www.joiintapp.com/";
    public static final String api_url = "https://joiintapp.com/api/user";

    public static final String REDIRECT_URL = base + "api/coinstock/";

    public static final String login = REDIRECT_URL+"oauth/token ";
    public static final String register = REDIRECT_URL+"signup";

    public static final String CHECK_MAIL_ALREADY_REGISTERED = api_url+"verify";
    public static final String RESET_PASSWORD = api_url + "reset/password";
    public static final String CHANGE_PASSWORD = api_url + "change/password";
    public static final String FORGOT_PASSWORD = api_url + "forgot/password";
    public static final String LOGOUT = api_url + "logout";
    public static final String HELP = REDIRECT_URL + "help";

    public static final String UserProfile = REDIRECT_URL+"profile";
    public static final String UseProfileUpdate = REDIRECT_URL+"profile/update";
    public static final String GET_USER_BALANCES = REDIRECT_URL + "balances";

    public static final String COIN_DEPOSIT = REDIRECT_URL + "coin/deposit";
    public static final String COIN_TRADE = REDIRECT_URL + "coin/trade";
    public static final String COIN_TRADE_LIST = REDIRECT_URL + "coin/tradablelist";
    public static final String COIN_TRADE_DATA = REDIRECT_URL + "coin/trade/data";
    public static final String COIN_TRADE_HISTORY_DATA = REDIRECT_URL + "coin/trade/history";
    public static final String COIN_TRADE_ORDERS = REDIRECT_URL + "coin/trade/orders";
    public static final String COIN_TRADE_CANCEL = REDIRECT_URL + "coin/trade/cancel";
    public static final String GET_ALL_COINS = REDIRECT_URL + "coins";
    public static final String COIN_EXCHANGE = REDIRECT_URL + "coin/exchange";
    public static final String GET_COIN_EXCHANGE_LIST = REDIRECT_URL + "coin/exchange/list";
    public static final String GET_COIN_EXCHANGE_RATE = REDIRECT_URL + "coin/exchange/rate";
    public static final String COIN_WITHDRAW = REDIRECT_URL + "coin/withdraw";
    public static final String GET_WITHDRAWBLE_COIN_ASSETS = REDIRECT_URL + "coin/withdraw_assets";
    public static final String TRANSFER_COIN = REDIRECT_URL + "coin/transfer";
    public static final String TRANSFER_COIN_HISTORY = REDIRECT_URL + "coin/transfer/history";
    public static final String GET_USDC_BALANCE = REDIRECT_URL + "usdc/balance";
    public static final String GET_HOME_DATA = REDIRECT_URL + "home";
    public static final String ADD_TRANSFER_COIN_CONTACT = REDIRECT_URL + "coin/transfer/contact/add";
    public static final String REMOVE_TRANSFER_COIN_CONTACT = REDIRECT_URL + "coin/transfer/contact/remove";

    public static final String GET_ALL_STOCKS = REDIRECT_URL + "stocks";
    public static final String GET_ALL_STOCKS_DAILY = REDIRECT_URL + "stocks/search";
    public static final String GET_ALL_STOCKS_AGGREGATE = REDIRECT_URL + "stocks/aggregates";
    public static final String GET_STOCK_DETAIL = REDIRECT_URL + "stock/detail";
    public static final String GET_STOCK_NEWS = REDIRECT_URL + "stock/news/all";
    public static final String REQUEST_STOCK_ORDER_CREATE = REDIRECT_URL + "stock/order/create";
    public static final String REQUEST_STOCK_ORDER_REPLACE = REDIRECT_URL + "stock/order/replace";
    public static final String REQUEST_STOCK_ORDER_CANCEL = REDIRECT_URL + "stock/order/cancel";
    public static final String GET_STOCK_ORDER_INVESTED = REDIRECT_URL + "stock/order/status/user/invested";
    public static final String GET_STOCK_ORDER_PENDING = REDIRECT_URL + "stock/order/status/user/pending";
    public static final String GET_STOCK_ORDER = REDIRECT_URL + "stock/order/status/user";
    public static final String STOCK_WITHDRAW = REDIRECT_URL + "stock/withdraw";
    public static final String REQUEST_DEPOSIT_STOCK = REDIRECT_URL + "stock/deposit";
    public static final String GET_DEPOSIT_STOCK_HISTORY = REDIRECT_URL + "stock/deposit/history";
//    public static final String GET_DEPOSIT_COIN_STOCK_HISTORY = REDIRECT_URL + "stock/deposit/history/coin";
//    public static final String GET_DEPOSIT_BANK_STOCK_HISTORY = REDIRECT_URL + "stock/deposit/history/bank";

    public static final String REQUEST_CARD = REDIRECT_URL + "card";
    public static final String REQUEST_PAYPAL = REDIRECT_URL + "paypal";

    public static final String REQUEST_STRIPE_CONNECT = REDIRECT_URL + "stripe/connect";

    public static final String GET_PLAID_LINK_TOKEN = REDIRECT_URL + "plaid/linktoken";
    public static final String SEND_PLAID_PUB_TOKEN = REDIRECT_URL + "plaid/pubtoken";
    public static final String SEND_PLAID_CONNECT_BANK = REDIRECT_URL + "plaid/bank/create";

    public static final String GET_BANK_DETAIL = REDIRECT_URL + "bank/detail";
    public static final String REQUEST_ADD_BANK = REDIRECT_URL + "bank/add";
    public static final String REQUEST_REMOVE_FRIEND_BANK = REDIRECT_URL + "bank/friend/remove";
    public static final String REQUEST_ADD_IBAN = REDIRECT_URL + "bank/assign/iban";
    public static final String REQUEST_ADD_FRIEND_BANK = REDIRECT_URL + "bank/friend/add";
    public static final String REQUEST_ADD_MONEY = REDIRECT_URL + "bank/money/add";
    public static final String REQUEST_SEND_MONEY = REDIRECT_URL + "bank/money/send";
    public static final String REQUEST_CONVERSION_RATE = REDIRECT_URL + "bank/currency/rate";
    public static final String REQUEST_FRIEND_BANK_LIST = REDIRECT_URL + "bank/friend/list";

    public static final String GET_PREDICTABLE_LIST = REDIRECT_URL + "predictable/list";
    public static final String REQUEST_PREDICT = REDIRECT_URL + "predict";
    public static final String REQUEST_PREDICT_BID = REDIRECT_URL + "predict/bid";
    public static final String REQUEST_PREDICT_CANCEL = REDIRECT_URL + "predict/cancel";

    public static final String GET_STAKE_BALANCE = REDIRECT_URL + "stake/balance";
    public static final String GET_STAKE_LIST = REDIRECT_URL + "stake/list";
    public static final String REQUEST_STAKE = REDIRECT_URL + "stake";
    public static final String REQUEST_STAKE_RELEASE = REDIRECT_URL + "stake/release";

    public static final String GET_MTN_SERVICE = REDIRECT_URL + "mtn";
    public static final String GET_MTN_TRANSACTION = REDIRECT_URL + "mtn/transactions";
    public static final String REQUEST_MTN_PAY = REDIRECT_URL + "mtn/pay";
    public static final String REQUEST_MTN_TOPUP = REDIRECT_URL + "mtn/topup";
    public static final String REQUEST_MTN_CONVERT = REDIRECT_URL + "mtn/convert";

    public static final String ZABO_REDIRECT = REDIRECT_URL + "zaho/webhook";
    public static final String GET_ZABO_ACCOUNTS = REDIRECT_URL + "zaho/accounts";
    public static final String REQUEST_ZABO_ACCOUNTS = REDIRECT_URL + "zaho/accounts";
    public static final String GET_ZABO_ACCOUNT = REDIRECT_URL + "zaho/account/";
    public static final String REQUEST_ZABO_DEPOSIT = REDIRECT_URL + "zaho/deposit";
}
