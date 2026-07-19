console.log("[+] Hook fichiers charge");

var openPtr = Process.getModuleByName("libc.so").getExportByName("open");
var readPtr = Process.getModuleByName("libc.so").getExportByName("read");

console.log("[+] open trouvee a : " + openPtr);
console.log("[+] read trouvee a : " + readPtr);

Interceptor.attach(openPtr, {
  onEnter(args) {
    this.path = args[0].readUtf8String();
    console.log("[+] open appelee : " + this.path);
  }
});

Interceptor.attach(readPtr, {
  onEnter(args) {
    console.log("[+] read appelee");
    console.log("    fd = " + args[0]);
    console.log("    taille = " + args[2].toInt32());
  }
});
