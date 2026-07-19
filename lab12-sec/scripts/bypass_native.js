/**
 * bypass_native.js
 * ─────────────────────────────────────────────────────────────
 * Native-layer (C/C++) root detection bypass for Android apps.
 * Hooks libc syscalls: open, openat, access, stat, lstat
 *
 * Use this alongside bypass_root.js for comprehensive coverage:
 *   frida -U -f <package.name> \
 *     -l bypass_root.js \
 *     -l bypass_native.js \
 *     --no-pause
 *
 * Lab 12 — Mobile Application Security
 * ─────────────────────────────────────────────────────────────
 */

"use strict";

// Paths that indicate root when accessed via native calls
const BLOCKED_PATHS = [
  "/system/bin/su",
  "/system/xbin/su",
  "/sbin/su",
  "/system/su",
  "/system/bin/busybox",
  "/system/xbin/busybox",
];

/**
 * Reads a C string from a NativePointer and checks if it
 * matches any known root-indicator path.
 */
function isSuspicious(pathPtr) {
  try {
    const s = pathPtr.readCString();
    if (!s) return false;
    return (
      BLOCKED_PATHS.indexOf(s) !== -1 ||
      s.includes("/proc/mounts") ||
      s.includes("/proc/self/mounts") ||
      s.includes("/proc/self/status")   // Some apps check TracerPid here
    );
  } catch (_) {
    return false;
  }
}

/**
 * Attaches an Interceptor to a named libc export.
 * @param {string} name       - Export symbol name (e.g. "open")
 * @param {number} pathArgIdx - Index of the path argument (0-based)
 */
function hookNative(name, pathArgIdx) {
  try {
    const addr = Module.getExportByName(null, name);
    Interceptor.attach(addr, {
      onEnter(args) {
        const pp = pathArgIdx >= 0 ? args[pathArgIdx] : null;
        if (pp && isSuspicious(pp)) {
          this.block = true;
          this.path  = pp.readCString();
        }
      },
      onLeave(ret) {
        if (this.block) {
          console.log(`[+] Blocked ${name}("${this.path}") → returning -1`);
          ret.replace(ptr(-1));   // Simulate ENOENT / EACCES
        }
      },
    });
    console.log(`[+] Hooked native: ${name}`);
  } catch (e) {
    console.log(`[-] Failed to hook ${name}: ${e.message}`);
  }
}

// ── Apply hooks to all relevant syscall wrappers ───────────
console.log("\n[*] bypass_native.js — Native hooks loading...\n");

hookNative("open",    0);   // open(path, flags, ...)
hookNative("openat",  1);   // openat(dirfd, path, flags, ...)
hookNative("access",  0);   // access(path, mode)
hookNative("stat",    0);   // stat(path, statbuf)
hookNative("lstat",   0);   // lstat(path, statbuf)
hookNative("stat64",  0);   // stat64 variant (some devices)
hookNative("lstat64", 0);   // lstat64 variant

console.log("\n[*] Native-layer root bypass fully active.\n");
