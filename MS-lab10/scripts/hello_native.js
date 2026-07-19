console.log("[+] Script charge");

var recvPtr = Process.getModuleByName("libc.so").getExportByName("recv");
console.log("[+] recv trouvee a : " + recvPtr);

Interceptor.attach(recvPtr, {
  onEnter(args) {
    console.log("[+] recv appelee");
  }
});