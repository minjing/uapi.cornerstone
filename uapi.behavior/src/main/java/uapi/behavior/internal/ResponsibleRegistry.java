/*
 * Copyright (C) 2017. The UAPI Authors
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at the LICENSE file.
 *
 * You must gained the permission from the authors if you want to
 * use the project into a commercial product
 */

package uapi.behavior.internal;

import jdk.nashorn.api.scripting.ScriptObjectMirror;
import uapi.GeneralException;
import uapi.behavior.IBehaviorRepository;
import uapi.behavior.IResponsible;
import uapi.config.annotation.Config;
import uapi.event.IEventBus;
import uapi.log.ILogger;
import uapi.rx.Looper;
import uapi.service.annotation.Init;
import uapi.service.annotation.Inject;
import uapi.service.annotation.Service;
import uapi.service.annotation.Tag;

import javax.script.*;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

/**
 * Read js files and generate Responsible
 */
@Service
@Tag("Behavior")
public class ResponsibleRegistry {

    @Config(path="path.behavior", optional=true)
    protected String _behaviorDefPath;

    @Inject
    protected ILogger _logger;

    @Inject
    protected List<IResponsible> _responsibles = new LinkedList<>();

    @Inject
    protected IEventBus _eventBus;

    @Inject
    protected IBehaviorRepository _behaviorRepo;

    @Init
    public void init() {
        // Load js based responsible if the config is specified
        if (this._behaviorDefPath != null) {
            File dir = new File(this._behaviorDefPath);
            if (!dir.exists()) {
                throw new GeneralException("The behavior definition directory is not exist - {}", this._behaviorDefPath);
            }
            if (!dir.isDirectory()) {
                throw new GeneralException("The behavior definition directory is not a directory - {}", this._behaviorDefPath);
            }

            // Initial javascript engine
            ScriptEngine jsEngine = new ScriptEngineManager().getEngineByName("nashorn");
            Bindings bindings = jsEngine.createBindings();
            bindings.put("registry", this);
            bindings.put("behaviorRepo", this._behaviorRepo);
            jsEngine.setBindings(bindings, ScriptContext.ENGINE_SCOPE);

            // Load all js file
            File[] jsFiles = dir.listFiles(file -> file.getName().endsWith(".js"));
            Looper.on(jsFiles).foreach(jsFile -> {
                try {
                    jsEngine.eval(new FileReader(jsFile));
                } catch (IOException | ScriptException ex) {
                    this._logger.error(ex);
                }
            });
        }

        // Register behavior/event handler into event bus
        Looper.on(this._responsibles)
                .flatmap(responsible -> Looper.on(responsible.behaviors()))
                .foreach(behavior -> this._eventBus.register(behavior));
    }

    public void register(ScriptObjectMirror mirror) {
        // Todo: invoked from javascript
    }
}
