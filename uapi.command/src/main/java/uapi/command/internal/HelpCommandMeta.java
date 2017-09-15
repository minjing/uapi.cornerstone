package uapi.command.internal;

import uapi.command.ICommandExecutor;
import uapi.command.ICommandMeta;
import uapi.command.CommandResult;
import uapi.command.IMessageOutput;
import uapi.common.ArgumentChecker;
import uapi.common.CollectionHelper;
import uapi.common.StringHelper;
import uapi.rx.Looper;

public class HelpCommandMeta implements ICommandMeta {

    public static final String NAME             = "help";

    public static final String NAME_INDENT      = StringHelper.duplicate(" ", 4);
    public static final int WIDTH_NAME_COLUMN   = 24;

    private final String _parentPath;
    private final ICommand _cmd;

    public HelpCommandMeta(ICommand command) {
        this(ROOT_PATH, command);
    }

    public HelpCommandMeta(
            final String parentPath,
            final ICommand command
    ) {
        this._parentPath = parentPath;
        this._cmd = command;
    }

    @Override
    public String parentPath() {
        return this._parentPath;
    }

    @Override
    public String name() {
        return NAME;
    }

    @Override
    public String description() {
        return null;
    }

    @Override
    public ICommandExecutor newExecutor() {
        return null;
    }

    private final class HelpCommandExecutor implements ICommandExecutor {

        private IMessageOutput _msgOut;

        @Override
        public String commandId() {
            return HelpCommandMeta.this.id();
        }

        @Override
        public void setMessageOutput(IMessageOutput output) {
            this._msgOut = output;
        }

        @Override
        public CommandResult execute() {
            ICommand command = HelpCommandMeta.this._cmd;
            if (! command.namespace().equals(ICommandMeta.DEFAULT_NAMESPACE)) {
                this._msgOut.output(StringHelper.makeString("Namespace: {}\n", command.namespace()));
            }
            if (! ArgumentChecker.isEmpty(command.name())) {
                this._msgOut.output(StringHelper.makeString(
                        "Usage: {} {} {} {} {}\n",
                        CollectionHelper.asString(command.ancestors(), " "),
                        command.name(),
                        command.availableParameters().length != 0 ? "[parameters]" : "",
                        command.availableOptions().length != 0 ? "[options]" : ""
                ));
            }

            this._msgOut.output("\n");

            if (command.availableParameters().length != 0) {
                this._msgOut.output(StringHelper.makeString("Available parameters: \n"));
                Looper.on(command.availableParameters()).foreach(paramMeta -> {
                    this._msgOut.output(NAME_INDENT);
                    if (paramMeta.name().length() <= WIDTH_NAME_COLUMN) {
                        this._msgOut.output(StringHelper.makeString(
                                "{}{}{}{}{}\n",
                                NAME_INDENT,
                                paramMeta.name(),
                                StringHelper.duplicate(" ", WIDTH_NAME_COLUMN - paramMeta.name().length()),
                                paramMeta.required() ? "" : "optional, ",
                                paramMeta.description()));
                    } else {
                        this._msgOut.output(StringHelper.makeString(
                                "{}{}\n{}{}{}\n",
                                NAME_INDENT,
                                paramMeta.name(),
                                StringHelper.duplicate("", WIDTH_NAME_COLUMN + NAME_INDENT.length()),
                                paramMeta.required() ? "" : "optional, ",
                                paramMeta.description()
                        ));
                    }
                });
            }

            this._msgOut.output("\n");

            if (command.availableParameters().length != 0) {
                this._msgOut.output(StringHelper.makeString("Available options: \n"));
                Looper.on(command.availableOptions()).foreach(opt -> {
                    StringBuilder buffer = new StringBuilder();
                    boolean hasShortName = false;
                    if (opt.shortName() != 0) {
                        buffer.append(opt.shortName());
                        hasShortName = true;
                    }
                    if (! ArgumentChecker.isEmpty(opt.name())) {
                        if (hasShortName) {
                            buffer.append(", ").append(opt.name());
                        } else {
                            buffer.append(opt.name());
                        }
                    }
                    if (! ArgumentChecker.isEmpty(opt.argument())) {
                        buffer.append(" <").append(opt.argument()).append(">");
                    }
                    if (buffer.length() <= WIDTH_NAME_COLUMN) {
                        this._msgOut.output(StringHelper.makeString(
                                "{}{}{}{}\n",
                                NAME_INDENT,
                                buffer.toString(),
                                StringHelper.duplicate(" ", WIDTH_NAME_COLUMN - buffer.length()),
                                opt.description()
                        ));
                    } else {
                        this._msgOut.output(StringHelper.makeString(
                                "{}{}\n{}{}\n",
                                NAME_INDENT,
                                buffer.toString(),
                                StringHelper.duplicate(" ", WIDTH_NAME_COLUMN + NAME_INDENT.length()),
                                opt.description()
                        ));
                    }
                });
            }

            this._msgOut.output("\n");

            ICommand[] subCmds = command.availableSubCommands();
            if (subCmds.length != 0) {
                Looper.on(HelpCommandMeta.this._cmd).foreach(cmd -> {
                    if (cmd.name().length() <= WIDTH_NAME_COLUMN) {
                        this._msgOut.output(StringHelper.makeString(
                                "{}{}{}{}\n",
                                NAME_INDENT,
                                cmd.name(),
                                StringHelper.duplicate(" ", WIDTH_NAME_COLUMN - cmd.name().length()),
                                cmd.description()
                        ));
                    } else {
                        this._msgOut.output(StringHelper.makeString(
                                "{}{}\n{}{}\n",
                                NAME_INDENT,
                                cmd.name(),
                                StringHelper.duplicate(" ", WIDTH_NAME_COLUMN + NAME_INDENT.length()),
                                cmd.description()
                        ));
                    }
                });
            }

            return CommandResult.success();
        }
    }
}