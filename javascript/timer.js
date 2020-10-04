/** All the sounds the user can choose from */
const soundList = [
    ["Meditation Bell 1", "./sounds/meditation_bell_low.wav"],
    ["Meditation Bell 2", "./sounds/meditation_bell_high.wav"],
    ["Birds", "./sounds/birds.wav"],
    ["Cafe", "./sounds/cafe.wav"],
    ["Church Bell 1", "./sounds/church_bell_low.wav"],
    ["Church Bell 2", "./sounds/church_bell_low_2.wav"],
    ["Church Bell 3", "./sounds/church_bell_high.wav"],
    ["Pealing Bells", "./sounds/pealing_bells.wav"],
    ["Gibberish", "./sounds/gibberish.wav"],
    ["Seashore", "./sounds/seashore.wav"],
];

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
        this.primSound = 0;
        this.secSound = 1;

        this.intervals = [
            [30 * 60 * 1000], // 30 mins * 60 seconds * 1000 milliseconds
            [5 * 60 * 1000],
            [5 * 60 * 1000],
            [5 * 60 * 1000],
        ];
    }

    // Instance methods
    /** changes duration of an interval */
    setDuration(index, duration) {
        if (index < this.intervals.length) {
            this.intervals[index] = duration;
        }
    }
    /** adds an interval of a certain duration*/
    addInterval(duration = 30 * 60 * 1000) {
        this.intervals.push(duration);
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
    /** moves an interval left */
    moveIntervalLeft(index) {

        let moved = false;

        if (index > 0 && index < this.intervals.length) {
            let temp = this.intervals[index - 1];
            this.intervals[index - 1] = this.intervals[index];
            this.intervals[index] = temp;

            moved = true;
        }

        return moved;
    }
    /** moves an interval left */
    moveIntervalRight(index) {

        let moved = false;

        if (index < this.intervals.length - 1) {
            let temp = this.intervals[index + 1];
            this.intervals[index + 1] = this.intervals[index];
            this.intervals[index] = temp;

            moved = true;
        }

        return moved;
    }
}

// ---------------
// EVENT LISTENERS
// ---------------
/**
 * Listens for a listview element to come into focus (to be clicked or tabbed to)
 *  so that its duration can be shown and/or edited
 *  (have to use this method because the listview is made up of nested <divs>, not form elements)
 */
document.getElementById("lv-settings-intervals").addEventListener("focus", function(event) {

    if (event.target.parentNode.id === "lv-settings-intervals") { // so only the children are targeted

        let finalSiblingIndex = event.target.parentNode.childElementCount - 1;
        let numNextSiblings = 0;

        countSibling(event.target.nextElementSibling); // determine number of siblings after the target sibling

        display.focusedInterval = finalSiblingIndex - numNextSiblings;
        display.sync.duration(display.focusedInterval); // update the text field with this interval's duration

        function countSibling(node) {
            if (node != null) {
                numNextSiblings++;
                countSibling(node.nextElementSibling);
            }
        }
    }
}, true); // event capturing (NOT bubbling)

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
    
        if (event.target.dataset.hint != undefined || event.target.parentNode.dataset.hint != undefined) { // Target node has a hint to display? Target node's parent has a hint to display (for the intervals list view)?
    
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
})

// ---------
// FUNCTIONS
// ---------
/**
 * Sound components
 */
var sounds = {
    prim: {
        ele: null,
        play: function() {
            this.ele.currentTime = 0;
            this.ele.play();
        }
    },
    sec: {
        ele: null,
        play: function() {
            this.ele.currentTime = 0;
            this.ele.play();
        }
    },
    all: {
        pause: function() {
            sounds.prim.ele.pause();
            sounds.prim.ele.currentTime = 0;
            sounds.sec.ele.pause();
            sounds.sec.ele.currentTime = 0;
        }
    },
    init: function() {
        let ddPrimSounds = document.getElementById("dd-sounds-prim");
        let ddSecSounds = document.getElementById("dd-sounds-sec");
    
        // Populate the drop down lists
        soundList.forEach(sound => {
            let primOption = document.createElement("option");
            let secOption = document.createElement("option");
    
            primOption.value = "./" + sound[0];
            primOption.innerText = sound[0];
            secOption.value = "./" + sound[0];
            secOption.innerText = sound[0];
    
            ddPrimSounds.appendChild(primOption);
            ddSecSounds.appendChild(secOption);
        });
    
        // Create audio elements
        //  (audio src set in UI sync because it can change based on the user's choice)
        sounds.prim.ele = document.createElement("audio"); // primary sound audio element
        sounds.sec.ele = document.createElement("audio"); // secondary sound audio element
        document.body.append(sounds.prim.ele, sounds.sec.ele);
    }
}
// *** For modifying/updating the displaying
/** For modifying the display based on
 *      - the session currently loaded
 *      - the profiles currently saved
 *      - the timer state
 *      - the set duration
 */
var display = {
    focusedInterval: 0, // member of lv-settings-intervals which is focused (-1 = last member)
    sync: {
        toSession: function() {
            // Sync Intervals
            let lvIntervals = document.getElementById("lv-settings-intervals");
            lvIntervals.innerHTML = "";

            intervalNum = 1;
            session.intervals.forEach(interval => {

                let intervalDiv = document.createElement("div");
                intervalDiv.tabIndex = "0";
                intervalDiv.innerHTML = "Interval " + intervalNum + ": <br><br>" + display.formatDuration(interval);;

                lvIntervals.appendChild(intervalDiv);
                
                intervalNum++;
            });

            if (display.focusedInterval > -1) {
                display.setFocusToInterval(display.focusedInterval);
            } else {
                display.setFocusToInterval(session.intervals.length - 1);
            }

            // Sync Duration
            document.getElementById("tf-settings-dur").value = display.formatDuration(session.intervals[display.focusedInterval]);

            // Sync Sounds
            if (document.getElementById("dd-sounds-prim").selectedIndex != session.primSound || sounds.prim.ele.src === "") {
                document.getElementById("dd-sounds-prim").selectedIndex = session.primSound;
                sounds.prim.ele.src = soundList[session.primSound][1];
            }
            if (document.getElementById("dd-sounds-sec").selectedIndex != session.secSound || sounds.sec.ele.src === "") {
                document.getElementById("dd-sounds-sec").selectedIndex = session.secSound;
                sounds.sec.ele.src = soundList[session.secSound][1];
            }
        },
        timeoutID: 0,
        toTimerState: function() {
            const startButton = document.getElementById("btn-timer-start");
            const endButton = document.getElementById("btn-timer-end");
            const pauseButton = document.getElementById("btn-timer-pause");
            const continueButton = document.getElementById("btn-timer-continue");
        
            if (timer.isOn) {
        
                display.disableElements(true); // so they can't be clicked + clearer indication that timer is on
        
                if (timer.isPaused) {
                    startButton.style.opacity = 0;
                    clearTimeout(this.timeoutID);
                    startButton.style.display = "none";
                    endButton.style.display = "grid";
                    pauseButton.style.display = "none";
                    continueButton.style.display = "grid";
                } else { // timer is running (i.e. timer.isOn && !timer.isPaused)
                    startButton.style.opacity = 0;
                    startButton.style.pointerEvents = "none";
                    this.timeoutID = setTimeout(() => (startButton.style.display = "none"), 3000); // have to do this so that things below it can be clicked AND so it can't be tabbed to
                    endButton.style.display = "grid";
                    pauseButton.style.display = "grid";
                    continueButton.style.display = "none";
                }
            } else { // timer is off (i.e. !timer.isOn)
        
                display.disableElements(false); // show the elements
        
                startButton.style.opacity = 1;
                startButton.style.pointerEvents = "auto";
                clearTimeout(this.timeoutID);
                startButton.style.display = "grid";
                endButton.style.display = "none";
                pauseButton.style.display = "none";
                continueButton.style.display = "none";
            }
        },
        toTimerProgress: function(percent) {
            const canvas = document.getElementById("sb-timer-canvas");
            const ctx = canvas.getContext("2d");

            if (percent === -1) {
                ctx.clearRect(0, 0, canvas.width, canvas.width);
            } else {
                ctx.clearRect(0, 0, canvas.width, canvas.width);
        
                // these angles are in degrees i will never understand radians why will i never understand radians???
                const startAngle = -90;
                const endAngle = 270;
                const currentAngle = (startAngle - endAngle) * (1 - percent) + startAngle;
        
                ctx.beginPath();
                ctx.moveTo(canvas.width / 2, canvas.width / 2);
                ctx.arc(canvas.width / 2, canvas.width / 2, canvas.width / 2, (Math.PI / 180) * startAngle, (Math.PI / 180) * currentAngle, false);
                ctx.fillStyle = "rgba(255, 255, 255, 0.4)";
                ctx.fill();
            }
        },
        toProfiles: function() {
            const ddProfiles = document.getElementById("dd-profiles-list").children;
            const defaultProfile = profilesStorage.getItem("default") || 0;
        
            for (let profileNum = 0; profileNum < ddProfiles.length; profileNum+=1) {
        
                let listEntry = "Profile";
        
                const sessionJSON = profilesStorage.getItem("Profile-" + profileNum);
        
                if (sessionJSON != null) {
                    const dateStr = profilesStorage.getItem("Profile-" + profileNum + "-date");
                    const date = new Date(parseInt(dateStr));
        
                    // listEntry += date.getFullYear() + "/" + date.getMonth() + "/" + date.getDay() + " (" + date.getHours() + ":" + date.getMinutes() + ")";
                    listEntry += ` (Saved ${date.getFullYear()}/${date.getMonth().toString().padStart(2, "0")}/${date.getDay().toString().padStart(2, "0")} at ${date.getHours().toString().padStart(2, "0")}:${date.getMinutes().toString().padStart(2, "0")})`;
        
                    if (profileNum == defaultProfile) { // NOT === (defaultProfile is a string)
                        listEntry += " (Default)";
                    }
                } else {
                    listEntry += " (empty)";
                }
        
                ddProfiles[profileNum].innerHTML = listEntry;
            }
        },
        /**
         * Updates duration textfield based on the selected interval
         * 
         * @param {Number} index The index of the selected interval
         */
        duration(index) {
            document.getElementById("tf-settings-dur").value = display.formatDuration(session.intervals[index]);
        }
    },
    setFocusToInterval: function(index) {
        document.getElementById("lv-settings-intervals").childNodes[index].focus();
    },
    /**
     * Converts a duration in milliseconds to formatted string duration in minutes or seconds
     *  (less than a minute = show in seconds; anything equal to or above a minute = show in minutes)
     * 
     * @param {Number} intervalDuration the duration of the interval in milliseconds
     */
    formatDuration(intervalDuration) {
        let durFormatted;
        if (intervalDuration < 60000) { // display in seconds
            durFormatted = (intervalDuration / 1000) + " secs";
        } else { // display in minutes
            durFormatted = (intervalDuration / (60 * 1000)).toFixed(2) + " mins";
        }
    
        return durFormatted;
    },
    disableElements: function(disable) { // true = disable the elements, false = enable the elements
        document.getElementById("dd-profiles-list").disabled = disable;
        document.getElementById("btn-profiles-load").disabled = disable;
        document.getElementById("btn-profiles-save").disabled = disable;
        document.getElementById("btn-profiles-del").disabled = disable;
        document.getElementById("btn-profiles-setdef").disabled = disable;
        document.getElementById("tf-settings-dur").disabled = disable;
        document.getElementById("btn-settings-set").disabled = disable;
        document.getElementById("btn-settings-move-ivl-left").disabled = disable;
        document.getElementById("btn-settings-move-ivl-right").disabled = disable;
        document.getElementById("btn-settings-add-ivl").disabled = disable;
        document.getElementById("btn-settings-del-ivl").disabled = disable;
        document.getElementById("btn-sounds-play-prim").disabled = disable;
        document.getElementById("dd-sounds-prim").disabled = disable;
        document.getElementById("btn-sounds-play-sec").disabled = disable;
        document.getElementById("dd-sounds-sec").disabled = disable;
    }
}

// *** Timer object
var timer = {
    isOn: false, // bool
    isPaused: false, // bool
    setTime: 0, // Date
    startTime: 0, // Date
    pauseTime: 0, // Date
    endTime: 0, // Date
    totalTime: 0, // Number
    schedule: [], // array
    interval: { // JS interval
        id: 0, // id of the interval
        index: 1,
        handler: function() {
            let elapsedTime = Date.now() - timer.startTime + (timer.pauseTime - timer.setTime);

            if (elapsedTime >= timer.schedule[0] - 125 && elapsedTime <= timer.schedule[0] + 125) { // timer has hit an interval
                if (timer.schedule.length === 1) { // Timer has finished its schedule

                    sounds.all.pause();
                    sounds.prim.play();

                    timer.action.end();
                    return;
                } else { // Timer finished an interval: play secondary sound

                    display.setFocusToInterval(timer.interval.index); // as we move from one interval to another, focus on the current interval
                    timer.interval.index++;

                    sounds.sec.play();
                    timer.schedule[1] += timer.schedule[0];
                    timer.schedule.splice(0, 1);
                }
            }

            display.sync.toTimerProgress(elapsedTime / timer.totalTime);
            console.log(elapsedTime, timer.totalTime);
        }
    },
    action: {
        start: function() {
            // timer.schedule = [...session.intervals];
            timer.schedule = JSON.parse(JSON.stringify(session.intervals));
            
        
            sounds.prim.play();
        
            timer.isOn = true;
            timer.setTime = timer.pauseTime = timer.startTime = Date.now();
            timer.totalTime = timer.schedule.reduce((total, currentValue) => (total += parseInt(currentValue)), 0);
            display.setFocusToInterval(0);
            display.sync.toTimerState();
        
            timer.interval.id = setInterval(timer.interval.handler, 50);
        },
        pause: function() {
            // Stop any sound that is currently playing
            sounds.all.pause();

            // Update the display
            timer.isPaused = true;
            display.sync.toTimerState();

            timer.pauseTime = Date.now();
            clearInterval(timer.interval.id);
        },
        continue: function() {
            sounds.all.pause();
            sounds.prim.play();
        
            timer.isPaused = false;
            display.sync.toTimerState();
            
            timer.startTime = Date.now();
            timer.interval.id = setInterval(timer.interval.handler, 50);
        },
        end: function() {
            sounds.all.pause();
            sounds.prim.play();
        
            timer.isOn = false;
            timer.isPaused = false;
        
            display.sync.toTimerProgress(0.9999999); // so progress bar shows full
        
            timer.interval.index = 1;
            display.setFocusToInterval(0);
        
            display.sync.toTimerState();
        
            clearInterval(timer.interval.id);
        }
    }
}

// *** Handler functions (for UI components)
var handler = {
    profile: {
        load: function(resyncUINow = true) {
            /*
             * @param {Boolean} resyncUINow whether the UI should be resynced or not
             *  - this should be left alone except in the initialization function where it should be
             *      set to false (since display.sync.toSession is called later on in the initialization function)
             */
            let profileNum = document.getElementById("dd-profiles-list").selectedIndex;
        
            let sessionJSON;
            sessionJSON = profilesStorage.getItem("Profile-" + profileNum);
            
            if (sessionJSON != null) { // Use a saved session
                // Create the new session class
                instanceVars = JSON.parse(sessionJSON); // JSON doesn't store instance methods!!!
                session = new Session();
                session.primSound = instanceVars.primSound;
                session.secSound = instanceVars.secSound;
                session.intervals = instanceVars.intervals;
            } // Else: use pre-made session
        
            if (resyncUINow) {
                display.sync.toSession();
            }
        },
        save: function () {
            let sessionJSON = JSON.stringify(session);
            let profileNum = document.getElementById("dd-profiles-list").selectedIndex;

            profilesStorage.setItem("Profile-" + profileNum, sessionJSON);
            profilesStorage.setItem("Profile-" + profileNum + "-date", Date.now().toString());

            display.sync.toProfiles();
        },
        del: function() {
            let profileNum = document.getElementById("dd-profiles-list").selectedIndex;

            profilesStorage.removeItem("Profile-" + profileNum);
            profilesStorage.removeItem("Profile-" + profileNum + "-date");
        
            if (profilesStorage.getItem("default") == profileNum) {
                profilesStorage.removeItem("default");
            }
        
            display.sync.toProfiles();
        },
        setDef: function() {
            let profileNum = document.getElementById("dd-profiles-list").selectedIndex;
            let sessionJSON = profilesStorage.getItem("Profile-" + profileNum);
        
            if (sessionJSON != null) {
                this.load(true);
                profilesStorage.setItem("default", profileNum);
        
                display.sync.toProfiles();
            }
        }
    },
    duration: {
        set: function() {
            let dur = document.getElementById("tf-settings-dur").value;
            let num = parseFloat(dur);
        
            if (dur.includes("m")) { // assume minutes if there is an "m"
                num = num * 60 * 1000; // convert to ms
            } else if (dur.includes("s")) { // assume seconds
                num = num * 1000; // convert to secs
            } else { // assume minutes
                num = num * 60 * 1000; // convert to ms
            }
        
            dur = display.formatDuration(num);
            session.setDuration(display.focusedInterval, num);
            display.sync.toSession();
        }
    },
    interval: {
        moveLeft: function() {
            let moveSuccessful = session.moveIntervalLeft(display.focusedInterval);
            if (moveSuccessful) {
                display.focusedInterval--;
                display.sync.toSession();
            }
        },
        moveRight: function() {
            let moveSuccessful = session.moveIntervalRight(display.focusedInterval);
            if (moveSuccessful) {
                display.focusedInterval++;
                display.sync.toSession();
            }
        },
        add: function() {
            session.addInterval();
            display.focusedInterval = -1;
            display.sync.toSession();
        },
        del: function() {
            let delSuccessful = session.delInterval(display.focusedInterval);
            if (delSuccessful) {
                if (display.focusedInterval > 0) {
                    display.focusedInterval--;
                }
                display.sync.toSession();
            }
        }
    },
    sound: {
        setPrim: function() {
            let selectedSound = document.getElementById("dd-sounds-prim").selectedIndex;

            session.primSound = selectedSound;
            sounds.prim.ele.src = soundList[session.primSound][1];
            sounds.prim.ele.load();
        },
        playPrim: function() {
            sounds.prim.ele.play();
        },
        setSec: function() {
            let selectedSound = document.getElementById("dd-sounds-sec").selectedIndex;

            session.secSound = selectedSound;
            sounds.sec.ele.src = soundList[session.secSound][1];
            sounds.sec.ele.load();
        },
        playSec: function() {
            sounds.sec.ele.play();
        }
    }
}

// Initialize the page
var session;
session = new Session();

const profilesStorage = window.localStorage; // for storing saved Profiles

// Load the default profile
const ddProfiles = document.getElementById("dd-profiles-list");
const defaultProfile = profilesStorage.getItem("default") || 0;
ddProfiles.selectedIndex = defaultProfile;
handler.profile.load(false);

display.sync.toProfiles(true); // Sync Profile elements and buttons

sounds.init(); // Initialize sound elements and buttons

display.sync.toSession(); // Sync Session Settings buttons
display.sync.toTimerState(); // Sync Timer buttons