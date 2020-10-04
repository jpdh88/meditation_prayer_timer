// -------
// OBJECTS
// -------
// *** localStorage: save up to 5 settings in local storage
let profileStorage = {
    storage: window.localStorage,
    STORAGE_KEY: "med-timer-vue-v1-profile-",
    load: function(profileNum) {
        let json = this.storage.getItem(this.STORAGE_KEY + profileNum) || null;
        let object = JSON.parse(json);

        return object;
    },
    save: function(object, profileNum) {
        let json = JSON.stringify(object);
        this.storage.setItem(this.STORAGE_KEY + profileNum, json);
        this.storage.setItem(this.STORAGE_KEY + profileNum + "-date", Date.now().toString());
    },
    delete: function(profileNum) {
        this.storage.removeItem(this.STORAGE_KEY + profileNum);
        this.storage.removeItem(this.STORAGE_KEY + profileNum + "-date");

        if (this.storage.getItem(this.STORAGE_KEY + "default") == profileNum) {
            this.storage.removeItem(this.STORAGE_KEY + "default");
        }
    },
    setDefault: function(profileNum) {
        this.storage.setItem(this.STORAGE_KEY + "default", profileNum);
    },
    getDefault: function() {
        let defaultProfileNum = this.storage.getItem(this.STORAGE_KEY + "default") || -1;

        return parseInt(defaultProfileNum);
    },
    getDate: function(profileNum) {
        let date = this.storage.getItem(this.STORAGE_KEY + profileNum + "-date") || "";

        return date;
    }
}
// *** Timer object
let timer = {
    prop: {
        isOn: false, // bool
        isPaused: false, // bool
        setTime: 0, // Date
        startTime: 0, // Date
        pauseTime: 0, // Date
        endTime: 0, // Date
        totalTime: 0, // Number
        elapsedTime: 0,
        schedule: [], // array
        currentInterval: 1,
        sound: {
            pause: false,
            playPrim: false,
            playSec: false,
        }
    },
    interval: { // JS interval
        id: 0, // id of the interval
        handler: function() {
            timer.prop.elapsedTime = Date.now() - timer.prop.startTime + (timer.prop.pauseTime - timer.prop.setTime);

            if (timer.prop.elapsedTime >= timer.prop.schedule[0].duration - 125 && timer.prop.elapsedTime <= timer.prop.schedule[0].duration + 125) { // timer has hit an interval
                if (timer.prop.schedule.length === 1) { // Timer has finished its schedule

                    timer.prop.sound.pause = true;
                    timer.prop.sound.playPrim = true;

                    timer.action.end();
                    return;
                } else { // Timer finished an interval: play secondary sound

                    timer.prop.currentInterval++; // as we move from one interval to another, focus on the current interval

                    timer.prop.sound.playSec = true;

                    timer.prop.schedule[1].duration += timer.prop.schedule[0].duration;
                    timer.prop.schedule.splice(0, 1);
                }
            }

            // console.log(timer.prop.elapsedTime, timer.prop.totalTime, timer.prop.schedule);
        }
    },
    action: {
        start: function(session) {
            // timer.prop.schedule = [...session.intervals]; // shallow copy!!!!
            timer.prop.schedule = JSON.parse(JSON.stringify(session.intervals));
        
            timer.prop.sound.playPrim = true;
        
            timer.prop.isOn = true;
            timer.prop.setTime = timer.prop.pauseTime = timer.prop.startTime = Date.now();
            timer.prop.totalTime = timer.prop.schedule.reduce((total, schedItem) => (total += parseInt(schedItem.duration)), 0);
        
            timer.interval.id = setInterval(timer.interval.handler, 50);
        },
        pause: function() {
            timer.prop.sound.pause = true;

            timer.prop.isPaused = true;

            timer.prop.pauseTime = Date.now();
            clearInterval(timer.interval.id);
        },
        continue: function() {
            timer.prop.sound.pause = true;
            timer.prop.sound.playPrim = true;
        
            timer.prop.isPaused = false;
            
            timer.prop.startTime = Date.now();
            timer.interval.id = setInterval(timer.interval.handler, 50);
        },
        end: function() {
            timer.prop.sound.pause = true;
            timer.prop.sound.playPrim = true;
        
            timer.prop.isOn = false;
            timer.prop.isPaused = false;
        
            timer.prop.elapsedTime = 1;
            timer.prop.totalTime = 1;
            timer.prop.currentInterval = 1;
        
            clearInterval(timer.interval.id);
        }
    }
}

// -------
// CLASSES
// -------
/**
 * Defines a Session object
 */
class Session {
    // Instance variables
    // primSound;
    // secSound;
    // intervals;

    // Constructor Method
    constructor() { // default Session object
        this.primSound = 5;
        this.secSound = 7;
        this.highestID = 4;

        this.intervals = [
            // { duration: 30 * 60 * 1000 }, // 30 mins * 60 seconds * 1000 milliseconds
            // { duration: 5 * 60 * 1000 },
            // { duration: 5 * 60 * 1000 },
            // { duration: 5 * 60 * 1000 },
            { duration: 5 * 1000, id: 1 }, // 30 mins * 60 seconds * 1000 milliseconds
            { duration: 10 * 1000, id: 2 },
            { duration: 20 * 1000, id: 3 },
            { duration: 5 * 1000, id: 4 },
        ];
    }

    // Instance methods
    /** changes duration of an interval */
    setDuration(index, duration) {
        if (index < this.intervals.length && index >= 0 && duration != undefined) {
            const tempID = this.intervals[index].id;
            this.intervals.splice(index, 1, { duration: duration, id: tempID });
        }
    }
    /** adds an interval of a certain duration*/
    addInterval(duration = 30 * 60 * 1000) {
        this.highestID++;
        this.intervals.push({duration: duration, id: this.highestID});
    }
    /** deletes an interval */
    delInterval(index) {
        let intervalDeleted = false;

        if (index < this.intervals.length && this.intervals.length > 1) {
            this.intervals.splice(index, 1);
            intervalDeleted = true;
        }

        return intervalDeleted;
    }
    /** moves an interval one position towards the start of the array */
    moveIntervalLeft(index) {
        let wasMoved = false;

        if (index > 0 && index < this.intervals.length) {
            this.intervals.splice(index, 0, this.intervals.splice(index - 1, 1)[0]);
            wasMoved = true;
        }

        return wasMoved;
    }
    /** moves an interval one position towards the end of the array */
    moveIntervalRight(index) {
        let wasMoved = false;

        if (index < this.intervals.length - 1) {
            this.intervals.splice(index, 0, this.intervals.splice(index + 1, 1)[0]);
            wasMoved = true;
        }

        return wasMoved;
    }
}

// --------------
// Event listeners
// --------------
// *** loading screen
document.onreadystatechange = function() {
    if (document.readyState === "complete") {
        document.getElementById("bg-container").style.zIndex = -1;
        document.getElementById("loading-screen").style.display = "none";
        document.getElementById("app-container").style.opacity = 1;
    }
}
// *** profile list <detail><summary>: allow user to click anywhere to collapse the detail
document.body.addEventListener("click", function(e) {
    if (!document.getElementById("dd-profiles-list").contains(e.target) && document.getElementById("dd-profiles-list").open) {
        document.getElementById("dd-profiles-list").open = false;
    }
});
// *** hint box
/**
 * Listens for mouse movement over an element that has a data-hint attribute (or an element that
 *  has a parent with a data-hint attribute) and, if the hints checkbox is checked, will display
 *  that element's "hint"
 */
document.body.addEventListener("mouseover", function(event) {

    if (document.getElementById("cb-display-hints").checked) {
        const hintBoxEle = document.getElementById("hint-box");
        const offsetX = 20; // px
        const offsetY = 5; // px
    
        if (event.target.dataset.hint != undefined || event.target.parentNode.dataset.hint != undefined) { // Target node has a hint to display? Target node's parent has a hint to display?
    
            hintBoxEle.style.display = "grid";
            hintBoxEle.textContent = event.target.dataset.hint || event.target.parentNode.dataset.hint; // no parent and child will both have a data-hint attribute
    
            // Calculate position of hint box
            const targetBoundingBox = event.target.getBoundingClientRect() || event.target.parentNode.getBoundingClientRect(); // for positioning hint box's y-axis
            let hintBoxX = document.getElementById("app").getBoundingClientRect().left + offsetX; // display hint box on left of app
            if (targetBoundingBox.x >= window.innerWidth / 2) {
                hintBoxX = window.innerWidth / 2; // display hint box on right of app
            }
    
            let hintBoxY = targetBoundingBox.bottom + offsetY; // display hint box below target element
            hintBoxEle.style.top = hintBoxY + "px";
            hintBoxEle.style.bottom = "";
            if (targetBoundingBox.y >= window.innerHeight / 2) {
                hintBoxY = (window.innerHeight - targetBoundingBox.y) + offsetY; // display hint box above target element
                hintBoxEle.style.bottom = hintBoxY + "px";
                hintBoxEle.style.top = "";
            }
    
            hintBoxEle.style.left = hintBoxX + "px";
    
        } else {
            hintBoxEle.style.display = "none";
        }
    }
});

// ---------
// VUE STUFF
// ---------
// *** Components
Vue.component("interval", {
    props: {
        intervalNum: Number,
        duration: Number, // bound to interval.duration
    },
    data: function() {
            return {
                durationList: [],
                userDuration: 0,
                formattedUserDuration: 0,
                initValue: 0,
            };
    },
    created: function() {
        this.durationList = [
            5000, // 5s 
            10000, // 10s 
            20000, // 20s 
            30000, // 30s 
            45000, // 45s 
            60000, // 60s 
            120000, // 2mins 
            180000, // 3mins 
            240000, // 4mins 
            300000, // 5mins 
            600000, // 10mins 
            900000, // 15mins 
            1200000, // 20mins 
            1500000, // 25mins 
            1800000, // 30mins 
            2100000, // 35mins 
            2400000, // 40mins 
            2700000, // 45mins 
            3000000, // 50mins 
            3300000, // 55mins 
            3600000, // 1hr 
            4500000, // 1hr 15mins 
            5400000, // 1hr 30mins 
            6300000, // 1hr 45mins 
            7200000, // 2hr 
        ]
        this.userDuration = this.duration;
        this.formattedUserDuration = this.formatDuration(this.userDuration);
        this.initValue = this.durationToValue(this.userDuration);
    },
    template:
        // https://jsfiddle.net/mani04/bgzhw68m/ (formatting w/ v-model)
        `
        <div    v-if="duration != -1"
                class="comp-interval"
                tabindex="0"
                data-hint="This is an interval. A session is made up of one or more intervals.">
            <div class="comp-interval-name">{{ this.formattedUserDuration }}</div>
            <input  class="comp-interval-input"
                    type="range"
                    min="0"
                    max="24"
                    step="1"
                    value="initValue"
                    @input="userDuration = valueToDuration($event.target.value)"
                    @blur="sendDirective('change')"
                    data-hint="Change the duration of the interval"
            /> <!-- only have data sync on blur (otherwise too slow) -->
            <!-- Old attributes: min="30000" max="7200000" step="30000" -->
            <button type="button"
                    class="elt-widget elt-btn"
                    id="comp-interval-move-up"
                    @click="sendDirective('moveup')"
                    data-hint="Move this interval up.">
                <div class="triangle-up"></div>
            </button>
            <button type="button"
                    class="elt-widget elt-btn"
                    id="comp-interval-move-down"
                    @click="sendDirective('movedown')"
                    data-hint="Move this interval down">
                    <div class="triangle-down"></div>
            </button>
            <button type="button"
                    class="elt-widget elt-btn"
                    id="comp-interval-del"
                    @click="sendDirective('del')"
                    data-hint="Delete this interval">
                <div class="ex"></div>
            </button>
        </div>
        <div    v-else
                id="comp-interval-addone"
                data-hint="Add another interval.">
            <button type="button"
                    id="btn-addone"
                    @click="sendDirective('add')">
                &#65291;
            </button>
            <br><br>
        </div>
        `,
        // pattern="(\\d+(\\s|)(m)\\w*(\\s|,|)+\\d+(\\s|)(s)\\w*)|(\\d+(\\s|)(m)\\w*)|(\\d+(\\s|)(s)\\w*)|(\\d+)"
    watch: {
        userDuration: function() {
            this.formattedUserDuration = this.formatDuration(this.userDuration);
        },
    },
    methods: {
        sendDirective: function(action) {
            let directive = {};

            directive.type = action;
            directive.num = this.intervalNum;
            if (action == "change") {
                directive.dur = this.userDuration;
            } else if (action == "add") {
                directive.dur = 0;
            } else if (action == "del") {
                directive.dur = 0;
            } else if (action == "moveup") {
                directive.dur = 0;
            } else if (action == "movedown") {
                directive.dur = 0;
            }

            this.$emit('send-directive', directive)
        },
        formatDuration: function(duration) {
            const hours = Math.floor( duration / 3600000 );
            const mins = Math.floor( (duration % 3600000) / 60000);
            const secs = Math.floor( ( (duration % 3600000) % 60000) / 1000);

            let output = "";
            if (hours != 0) {
                output += hours + "hrs ";
            }
            if (mins != 0) {
                output += mins + "mins ";
            }
            if (secs != 0) {
                output += secs + "secs";
            }

            return output;
        },
        durationToValue: function(duration) {
            return this.durationList.indexOf(duration);
        },
        valueToDuration: function(value) {
            // re: https://stackoverflow.com/questions/12300767/html-input-range-step-as-an-array-of-values
            return this.durationList[value];
        },
    },
});
Vue.component("profile", {
    props: {
        profileNum: Number,
        profileInfo: Object,
        currentProfile: Number,
        /**
         * Watcher in the Vue instance detects changes in the actOnProfile Object:
         *  - change in .action: "change", "save", "del", "setdef"
         *  - .profileNum will be the profile to be acted on
         */
        value: Object, // bound to actOnProfile.action
    },
    template:
        `
        <div    class="comp-profile"
                tabindex="0"
                @keyup.space="$emit('input', changeProfile())">
            <div @click="$emit('input', changeProfile())">
                <span v-if="currentProfile == profileNum">
                    &nbsp;&check;
                </span>
                <span class="comp-profile-name">
                    {{ profileInfo.name }}:
                    <span   style="font-size: smaller"
                            data-hint="The date this profile was saved.">{{ this.formattedDate }}</span>
                </span>
            </div>
            <button type="button"
                    class="comp-profile-save"
                    @click="$emit('input', saveProfile())"
                    @keyup.space="$emit('input', saveProfile())">
                <span   v-if="profileIsEmpty"
                        data-hint="Save this session.">SAVE</span>
                <span   v-else
                        data-hint="Overwrite the saved profile.">OVRWRT</span>
            </button>
            <button type="button"
                    class="comp-profile-setdef"
                    @click="$emit('input', setDefProfile())"
                    @keyup.space="$emit('input', setDefProfile())"
                    :disabled="profileIsEmpty || profileInfo.def == true">
                <span   v-if="profileInfo.def == true"
                        data-hint="This is the default profile.">DEF</span>
                <span   v-else
                        data-hint="Set this profile as default.">SET DEF</span>
            </button>
            <button type="button"
                    class="comp-profile-del"
                    @click="$emit('input', delProfile())"
                    @keyup.space="$emit('input', delProfile())"
                    :disabled="profileIsEmpty"
                    data-hint="Delete this saved profile.">
                DEL
            </button>
        </div>
    `,
    computed: {
        formattedDate: function() {
            let dateStr = this.profileInfo.date;
            let formatted = "empty";

            if (dateStr != "") {
                let date = new Date(parseInt(dateStr));
                formatted = `(Saved ${date.getFullYear()}/${date.getMonth().toString().padStart(2, "0")}/${date.getDay().toString().padStart(2, "0")} at ${date.getHours().toString().padStart(2, "0")}:${date.getMinutes().toString().padStart(2, "0")})`;
            }
    
            return formatted;
        },
        profileIsEmpty: function() {
            return (this.formattedDate == "empty");
        }
    },
    methods: {
        changeProfile: function() {
            let newMsg = {};

            newMsg.num = this.profileNum;
            newMsg.action = "change";

            return newMsg;
        },
        saveProfile: function() {
            let newMsg = {};

            newMsg.num = this.profileNum;
            newMsg.action = "save";

            return newMsg;
        },
        delProfile: function() {
            let newMsg = {};

            newMsg.num = this.profileNum;
            newMsg.action = "del";

            return newMsg;
        },
        setDefProfile: function() {
            let newMsg = {};

            newMsg.num = this.profileNum;
            newMsg.action = "setdef";

            return newMsg;
        },
    },
});

// *** Main app
var app = new Vue({
    el: "#app",
    data: {
        sounds: {
            1: {    name: "Meditation Bell 1",  path: "./sounds/meditation_bell_low.wav" },
            2: {    name: "Meditation Bell 2",  path: "./sounds/meditation_bell_high.wav"},
            3: {    name: "Birds",              path: "./sounds/birds.wav"},
            4: {    name: "Cafe",               path: "./sounds/cafe.wav"},
            5: {    name: "Church Bell 1",      path: "./sounds/church_bell_low.wav"},
            6: {    name: "Church Bell 2",      path: "./sounds/church_bell_low_2.wav"},
            7: {    name: "Church Bell 3",      path: "./sounds/church_bell_high.wav"},
            8: {    name: "Pealing Bells",      path: "./sounds/pealing_bells.wav"},
            9: {    name: "Gibberish",          path: "./sounds/gibberish.wav"},
            10: {   name: "Seashore",           path: "./sounds/seashore.wav"},
        },
        profiles: {
            1: {name: "1", date: "", def: false},
            2: {name: "2", date: "", def: false},
            3: {name: "3", date: "", def: false},
            4: {name: "4", date: "", def: false},
            5: {name: "5", date: "", def: false},
        },
        session: new Session(),
        intervalSelected: 1,
        currentProfile: 1,
        actOnProfile: {
            action: "", // change, save, del, setdef
            num: -1,
        },
        actOnInterval: {
            type: "", // add, change, delete, moveup, movedown
            num: -1,
            dur: 0,
        },
        timerProps: timer.prop,
    },
    watch: {
        // Which interval to focus on?
        "intervalSelected": function() {
            document.getElementsByClassName("interval")[this.intervalSelected - 1].focus();
        },
        // user has chosen to act on an interval (change, add, delete, moveup, movedown)
        "actOnInterval": function() {
            if (this.actOnInterval.type == "change") {
                console.log("luggggg");
                this.setIntervalDuration(this.actOnInterval.num, this.actOnInterval.dur);
            } else if (this.actOnInterval.type == "add") {
                this.addInterval();
            } else if (this.actOnInterval.type == "del") {
                this.delInterval(this.actOnInterval.num);
            } else if (this.actOnInterval.type == "moveup") {
                this.moveIntervalUp(this.actOnInterval.num);
            } else if (this.actOnInterval.type == "movedown") {
                this.moveIntervalDown(this.actOnInterval.num);
            }
        },
        // User has chosen to act on a profile (change, save, delete, set to default)
        "actOnProfile": function() {
            if (this.actOnProfile.action == "change") {
                const changeTo = this.actOnProfile.num;
                
                this.currentProfile = changeTo;
            } else if (this.actOnProfile.action == "save") {
                const profileToSave = this.actOnProfile.num;

                this.profiles[profileToSave].date = Date.now();
                profileStorage.save(this.session, profileToSave);
            } else if (this.actOnProfile.action == "del") {
                const profileToDelete = this.actOnProfile.num;

                this.profiles[profileToDelete].date = "";
                this.profiles[profileToDelete].def = false;
                profileStorage.delete(profileToDelete);
                if (profileToDelete == this.currentProfile) {
                    this.currentProfile = -1;
                }
            } else if (this.actOnProfile.action == "setdef") {
                profileStorage.setDefault(this.actOnProfile.num);

                for (let key of Object.keys(this.profiles)) {
                    if (key == this.actOnProfile.num) {
                        this.profiles[key].def = true;
                    } else {
                        this.profiles[key].def = false;
                    }
                }
            }
        },
        // the current profile has changed: load it
        "currentProfile": function() {
            let loadedSession = profileStorage.load(this.currentProfile);

            if (loadedSession != null) {
                this.session.primSound = loadedSession.primSound;
                this.session.secSound = loadedSession.secSound;
                this.session.intervals = loadedSession.intervals;
            } else {
                this.session = new Session();
            }

            // Load the chosen sounds
            this.loadPrimSound();
            this.loadSecSound();
        },
        // Timer flags
        "timerProps.currentInterval": function() {
            this.intervalSelected = this.timerProps.currentInterval;
        },
        "timerProps.sound.pause": function() {
            if (this.timerProps.sound.pause) {
                this.pauseSounds();
                this.timerProps.sound.pause = false;
            }
        },
        "timerProps.sound.playPrim": function() {
            if (this.timerProps.sound.playPrim) {
                this.playPrimSound();
                this.timerProps.sound.playPrim = false;
            }
        },
        "timerProps.sound.playSec": function() {
            if (this.timerProps.sound.playSec) {
                this.playSecSound();
                this.timerProps.sound.playSec = false;
            }
        },
        "timerProps.elapsedTime": function() {
            const sbTimerEle = document.getElementById("sb-timer");
            const percent = this.timerProps.elapsedTime / this.timerProps.totalTime;

            // console.log(percent, sbTimerEle.style.height);

            sbTimerEle.style.height = (330 * percent) + "px";
            sbTimerEle.style.opacity = 1;

            if (percent == 1) {
                sbTimerEle.style.height = "330px";
                sbTimerEle.style.opacity = 0;
                this.endTimer();
            }
        }
    },
    methods: {
        loadPrimSound: function() {
            document.getElementById("audio-sound-prim").src = this.sounds[this.session.primSound].path;
            document.getElementById("audio-sound-prim").load();
        },
        loadSecSound: function() {
            document.getElementById("audio-sound-sec").src = this.sounds[this.session.secSound].path;
            document.getElementById("audio-sound-sec").load();
        },
        playPrimSound: function() {
            document.getElementById("audio-sound-prim").currentTime = 0;
            document.getElementById("audio-sound-prim").play();
        },
        playSecSound: function() {
            document.getElementById("audio-sound-sec").currentTime = 0;
            document.getElementById("audio-sound-sec").play();
        },
        pauseSounds: function() {
            document.getElementById("audio-sound-prim").pause();
            document.getElementById("audio-sound-prim").currentTime = 0;
            document.getElementById("audio-sound-sec").pause();
            document.getElementById("audio-sound-sec").currentTime = 0;
        },
        addInterval: function() {
            this.session.addInterval();
        },
        setIntervalDuration: function(intervalNum, duration) {
            this.session.setDuration(intervalNum - 1, duration);
        },
        delInterval: function(intervalNum) {
            this.session.delInterval(intervalNum - 1);
        },
        moveIntervalUp: function(intervalNum) {
            this.session.moveIntervalLeft(intervalNum - 1);
        },
        moveIntervalDown: function(intervalNum) {
            this.session.moveIntervalRight(intervalNum - 1);
        },
        startTimer: function() {
            timer.action.start(this.session);
            intervalSelected = 1;
        },
        pauseTimer: function() {
            timer.action.pause();
        },
        continueTimer: function() {
            timer.action.continue();
        },
        endTimer: function() {
            timer.action.end();
        },
        changeIntervalSelected: function(intervalNum) {
            this.intervalSelected = intervalNum;
        },
        formatDate: function(dateStr) {
            let date;
            let formatted = "(empty)";

            if (dateStr != "") {
                date = new Date(parseInt(dateStr));
                formatted = `(Saved ${date.getFullYear()}/${date.getMonth().toString().padStart(2, "0")}/${date.getDay().toString().padStart(2, "0")} at ${date.getHours().toString().padStart(2, "0")}:${date.getMinutes().toString().padStart(2, "0")})`;
            }
    
            return formatted;
        },
    },
    created: function() {
        // initialize the profiles
        let defaultProfileNum = profileStorage.getDefault();
        for (let key of Object.keys(this.profiles)) {
            let date = profileStorage.getDate(key);
            this.profiles[key].date = date;
            
            if(key == defaultProfileNum) {
                this.profiles[key].def = true;
            } else {
                this.profiles[key].def = false;
            }
        }
        this.currentProfile = defaultProfileNum;
    },
})