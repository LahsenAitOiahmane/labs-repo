console.log("[+] Hook connect charge");

var connectPtr = Process.getModuleByName("libc.so").getExportByName("connect");
console.log("[+] connect trouvee a : " + connectPtr);

Interceptor.attach(connectPtr, {
  onEnter(args) {
    console.log("[+] connect appelee");
    console.log("    fd = " + args[0]);
    console.log("    sockaddr = " + args[1]);
  },
  onLeave(retval) {
    console.log("    retour = " + retval.toInt32());
  }
});
