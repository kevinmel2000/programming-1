'''
Created on Dec 24, 2012
stackoverflow.com/questions/739654/understanding-python-decorators

THE POINT HERE!:
 @DECORATOR is (literally, see bottom) a shortcut to
   hello = DECORATOR(hello)
'''

def makebold(fn):
    def wrapped(): # Doesn't HAVE to be called this, can be whatever
        return "<b>" + fn() + "</b>"
    return wrapped

def makeitalic(fn):
    def wrapped():
        return "<i>" + fn() + "</i>"
    return wrapped

@makebold
@makeitalic
def hello():
    return "Hello"

print hello()

def hello2():
    return "Hello"

print hello2()
hello2 = makebold(makeitalic(hello2))   # this is equivalent
print hello2()