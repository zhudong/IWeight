package com.axecom.iweight.bean;

import com.axecom.iweight.base.BaseEntity;

import java.util.List;

public class LocalSettingsBean<T> extends BaseEntity {
    public Value value;

    public List<CardReaderTypeList> card_reader_type_list;
    public List<WeightPort> weight_port;
    public List<PrinterPort> printer_port;
    public List<ExternalLedPort> external_led_port;
    public List<CardReaderPort> card_reader_port;

    public class Value {
        public NumberOfPrintsConfiguration number_of_prints_configuration;
        public LotValidityTime lot_validity_time;
        public ClearTransactionData clear_transaction_data;
        public ScreenOff screen_off;
        public WeighingPlateBaudRate weighing_plate_baud_rate;
        public CardReaderType card_reader_type;
        public ServerIp server_ip;
        public ServerPort server_port;
        public ServerPort printer_port;
        public ServerPort weight_port;


        public class NumberOfPrintsConfiguration {
            public String val;
            public String update_time;
        }

        public class LotValidityTime {
            public String val;
            public String update_time;
        }
        public class WeightPort {
            public String val;
            public String update_time;
        }
        public class PrinterPort {
            public String val;
            public String update_time;
        }

        public class ClearTransactionData {
            public String val;
            public String update_time;
        }

        public class ScreenOff {
            public String val;
            public String update_time;
        }

        public class WeighingPlateBaudRate {
            public String val;
            public String update_time;
        }

        public class CardReaderType {
            public String val;
            public String update_time;
        }


        public class ServerIp {
            public String val;
            public String update_time;
        }

        public class ServerPort {
            public String val;
            public String update_time;
        }
    }

    public class CardReaderTypeList {
        public String val;
    }

    public class WeightPort {
        public String val;
    }

    public class PrinterPort {
        public String val;
    }

    public class ExternalLedPort {
        public String val;
    }

    public class CardReaderPort {
        public String val;
    }


}
