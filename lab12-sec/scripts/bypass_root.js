/**
 * bypass_root.js
 * ─────────────────────────────────────────────────────────────
 * Java-layer root detection bypass for Android applications.
 * Neutralizes: Build.TAGS, File.exists(), Runtime.exec(), RootBeer
 *
 * Usage:
 *   frida -U -f <package.name> -l bypass_root.js --no-pause
 *   frida -U --attach <package.name> -l bypass_root.js
 *
 * Lab 12 — Mobile Application Security
 * ─────────────────────────────────────────────────────────────
 */

"use strict";

// Known suspicious paths checked by root-detection routines
const SUSPICIOUS_PATHS = [
  "/system/bin/su",
  "/system/xbin/su",
  "/sbin/su",
  "/system/su",
  "/system/app/Superuser.apk",
  "/system/app/SuperSU.apk",
  "/system/bin/.ext/.su",
  "/system/usr/we-need-root/",
  "/system/xbin/daemonsu",
  "/system/etc/init.d/99SuperSUDaemon",
  "/system/bin/busybox",
  "/system/xbin/busybox",
];

// Checks if a command string targets root-related binaries
function isSuspiciousCommand(cmd) {
  const t = (cmd || "").toLowerCase().trim();
  return (
    t.startsWith("su") ||
    t.includes("which su") ||
    t.includes("busybox") ||
    / su( |$)/.test(t)
  );
}

Java.perform(function () {
  console.log("\n[*] bypass_root.js — Java-layer hooks loading...\n");

  // ── 1. android.os.Build.TAGS ───────────────────────────────
  try {
    const Build = Java.use("android.os.Build");
    Object.defineProperty(Build, "TAGS", {
      get: function () {
        return "release-keys";
      },
    });
    console.log("[+] Build.TAGS  →  release-keys");
  } catch (e) {
    console.log("[-] Build.TAGS hook failed:", e.message);
  }

  // ── 2. RootBeer library (scottyab) ────────────────────────
  try {
    const RB = Java.use("com.scottyab.rootbeer.RootBeer");
    RB.isRooted.implementation = function () {
      console.log("[+] RootBeer.isRooted()  →  false");
      return false;
    };
    if (RB.isRootedWithBusyBoxCheck) {
      RB.isRootedWithBusyBoxCheck.implementation = function () {
        console.log("[+] RootBeer.isRootedWithBusyBoxCheck()  →  false");
        return false;
      };
    }
  } catch (e) {
    // RootBeer may not be present in all apps — that's fine
  }

  // ── 3. java.io.File.exists() ──────────────────────────────
  try {
    const File = Java.use("java.io.File");
    File.exists.implementation = function () {
      const path = this.getAbsolutePath();
      if (SUSPICIOUS_PATHS.indexOf(path) !== -1) {
        console.log("[+] File.exists() blocked for:", path);
        return false;
      }
      return this.exists.call(this);
    };
    console.log("[+] File.exists()  →  hooks installed");
  } catch (e) {
    console.log("[-] File.exists hook failed:", e.message);
  }

  // ── 4. java.lang.Runtime.exec() — all overloads ───────────
  try {
    const Runtime  = Java.use("java.lang.Runtime");
    const JString  = Java.use("java.lang.String");
    const StrArray = Java.use("[Ljava.lang.String;");

    const safeCmd = ["sh", "-c", "echo", "blocked"];

    // exec(String)
    Runtime.exec.overload("java.lang.String").implementation = function (cmd) {
      if (isSuspiciousCommand(cmd)) {
        console.log("[+] Runtime.exec blocked (String):", cmd);
        return this.exec(JString.$new("echo blocked"));
      }
      return this.exec(cmd);
    };

    // exec(String[])
    Runtime.exec.overload("[Ljava.lang.String;").implementation = function (arr) {
      const js = arr ? Array.from(arr) : [];
      if (isSuspiciousCommand(js.join(" "))) {
        console.log("[+] Runtime.exec blocked (String[]):", js.join(" "));
        const a = StrArray.$new(safeCmd.length);
        for (let i = 0; i < safeCmd.length; i++) a[i] = JString.$new(safeCmd[i]);
        return this.exec(a);
      }
      return this.exec(arr);
    };

    // exec(String, String[])
    Runtime.exec
      .overload("java.lang.String", "[Ljava.lang.String;")
      .implementation = function (cmd, env) {
      if (isSuspiciousCommand(cmd)) {
        console.log("[+] Runtime.exec blocked (String, env):", cmd);
        return this.exec(JString.$new("echo blocked"), env);
      }
      return this.exec(cmd, env);
    };

    console.log("[+] Runtime.exec()  →  hooks installed (all overloads)");
  } catch (e) {
    console.log("[-] Runtime.exec hook failed:", e.message);
  }

  console.log("\n[*] Java-layer root bypass fully active.\n");
});
