package io.wiklandia.logger;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.AppenderBase;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AwsTimestreamAppender extends AppenderBase<ILoggingEvent> {

    private AwsSettings awsSettings;
    private AwsTimestreamEventLogger awsTimestreamEventLogger;

    @Override
    public void start() {
        this.awsTimestreamEventLogger = AwsTimestreamEventLogger.create(awsSettings);
        this.started = true;
    }

    @Override
    protected void append(ILoggingEvent event) {
        awsTimestreamEventLogger.writeLog(event);
    }


}
