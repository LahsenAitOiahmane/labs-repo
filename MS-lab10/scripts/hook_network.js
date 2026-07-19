console.log("[+] Hooks reseau charges");

var sendPtr = Process.getModuleByName("libc.so").getExportByName("send");
var recvPtr = Process.getModuleByName("libc.so").getExportByName("recv");

console.log("[+] send trouvee a : " + sendPtr);
console.log("[+] recv trouvee a : " + recvPtr);

Interceptor.attach(sendPtr, {
  onEnter(args) {
    console.log("[+] send appelee");
    console.log("    fd = " + args[0]);
    console.log("    len = " + args[2].toInt32());
  }
});

Interceptor.attach(recvPtr, {
  onEnter(args) {
    console.log("[+] recv appelee");
    console.log("    fd = " + args[0]);
    console.log("    len demande = " + args[2].toInt32());
  },
  onLeave(retval) {
    console.log("    recv retourne = " + retval.toInt32());
  }
});
