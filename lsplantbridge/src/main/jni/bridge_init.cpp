#include <jni.h>
#include <lsplant.hpp>
#include <android/log.h>
#include <dobby.h>
#include <dlfcn.h>
#include "elf/elf_image.h"
#include "elf/symbol_cache.h"
#include "common/logger.h"

#include <sys/mman.h>

#define _uintval(p)               reinterpret_cast<uintptr_t>(p)
#define _ptr(p)                   reinterpret_cast<void *>(p)
#define _align_up(x, n)           (((x) + ((n) - 1)) & ~((n) - 1))
#define _align_down(x, n)         ((x) & -(n))
#define _page_size                16384
#define _page_align(n)            _align_up(static_cast<uintptr_t>(n), _page_size)
#define _ptr_align(x)             _ptr(_align_down(reinterpret_cast<uintptr_t>(x), _page_size))
#define _make_rwx(p, n)           ::mprotect(_ptr_align(p), \
                                              _page_align(_uintval(p) + n) != _page_align(_uintval(p)) ? _page_align(n) + _page_size : _page_align(n), \
                                              PROT_READ | PROT_WRITE | PROT_EXEC)

void* InlineHooker(void* target, void* hooker) {
    _make_rwx(target, _page_size);
    void* origin_call;
    if (DobbyHook(target, hooker, &origin_call) == 0) {
        return origin_call;
    } else {
        return nullptr;
    }
}

bool InlineUnhooker(void* func) {
    return DobbyDestroy(func) == 0;
}

extern "C" JNIEXPORT jint JNICALL JNI_OnLoad(JavaVM *vm, void *reserved) {
    JNIEnv *env = nullptr;

    if (vm->GetEnv(reinterpret_cast<void **>(&env), JNI_VERSION_1_6) != JNI_OK) {
        return JNI_ERR;
    }

    lsplant::InitInfo info{
        .inline_hooker = InlineHooker,
        .inline_unhooker = InlineUnhooker,
        .art_symbol_resolver =
            [](auto symbol) { return vector::native::ElfSymbolCache::GetArt()->getSymbAddress(symbol); },
        .art_symbol_prefix_resolver =
            [](auto symbol) { return vector::native::ElfSymbolCache::GetArt()->getSymbPrefixFirstAddress(symbol); },
        .generated_class_name = "VirtualXposed_",
        .generated_source_name = "Dobby",
    };

    if (!lsplant::Init(env, info)) {
        LOG_INFO("Bridge error");
        // Initialization failed
        return JNI_ERR;
    }

    LOG_INFO("Bridge initialized");

    return JNI_VERSION_1_6;
}

// TODO Inline hooks? https://github.com/LSPosed/LSPlant/blob/master/test/src/main/jni/test.cpp

extern "C"
JNIEXPORT jobject JNICALL
Java_com_virtualxposed_lsplantbridge_MethodHooker_hookTarget(JNIEnv *env, jobject thiz, jobject target, jobject callback) {
    return lsplant::Hook(env, target, thiz, callback);
}
extern "C"
JNIEXPORT jboolean JNICALL
Java_com_virtualxposed_lsplantbridge_MethodHooker_unhookTarget(JNIEnv *env, jobject thiz, jobject target) {
    return lsplant::UnHook(env, target);
}