//
//  SetCard.m
//  Matchismo
//
//  Created by Ethan Petuchowski on 12/5/13.
//  Copyright (c) 2013 Ethan Petuchowski. All rights reserved.
//

#import "SetCard.h"

@implementation SetCard

/* could call [super match:...] but doesn't have to in this case */
- (int)match:(NSArray *)otherCards
{
    int matchScore = 0;
    NSMutableSet *chosenShapes = [NSMutableSet setWithObject:self.shape];
    NSMutableSet *chosenColors = [NSMutableSet setWithObject:self.color];
    NSMutableSet *chosenFills  = [NSMutableSet setWithObject:self.fillType];
    for (SetCard *otherCard in otherCards) {
        [chosenShapes addObject:otherCard.shape];
        [chosenColors addObject:otherCard.color];
        [chosenFills addObject:otherCard.fillType];
    }
    
    if (([chosenShapes count] == 3 || [chosenShapes count] == 1) &&
        ([chosenColors count] == 3 || [chosenColors count] == 1) &&
        ([chosenFills count]  == 3 || [chosenFills count]  == 1))
    {
        matchScore = 1;
    }
    
    return matchScore;
}


+ (NSDictionary *)colorDict
{
    return @{@"Red": [UIColor redColor],
             @"Blue": [UIColor blueColor],
             @"Green": [UIColor greenColor]};
}


+ (NSDictionary *)shapesDict
{
    return @{@"Square": @"◼︎",
             @"Triangle": @"▲",
             @"Circle": @"☯"};
}


- (NSAttributedString *)attributedContents
{
    NSMutableDictionary *attributes =
        [@{NSForegroundColorAttributeName:[SetCard colorDict][self.color]} mutableCopy];
    
    if ([self.fillType  isEqualToString: @"Backfilled"]) {
        [attributes addEntriesFromDictionary:@{NSBackgroundColorAttributeName: [UIColor grayColor]}];
    } else if ([self.fillType  isEqualToString: @"Outlined"]) {
        [attributes addEntriesFromDictionary:@{NSBackgroundColorAttributeName: [UIColor yellowColor]}];
    }
    
    return [[NSAttributedString alloc] initWithString:[SetCard shapesDict][self.shape] attributes:attributes];
}

- (NSString *)contents
{   // http://stackoverflow.com/questions/510269/how-do-i-concatenate-strings
    NSArray *stringArray = [NSArray arrayWithObjects:[self.color substringToIndex:1], [self.fillType substringToIndex:1], [SetCard shapesDict][self.shape], @",", nil];
    return [stringArray componentsJoinedByString:@""];
}

/* TODO: These should be done with enums; it would look like this:
 * enum shapes { oval, diamont, squiggle };
 * Then (I guess) you could say something like
 *      if (card.shape == card.oval) { ... }
 */
+ (NSArray *)validShapes
{
    return @[@"Square", @"Triangle", @"Circle"];
}

// TODO enum colors { green, red, purple };
+ (NSArray *)validColors
{
    return @[@"Red", @"Blue", @"Green"];
}

// TODO enum fills { solid, striped, unfilled };
+ (NSArray *)validFillTypes
{
    return @[@"Normal", @"Outlined", @"Backfilled"];
}

// must @synthesize properties if we provide both setter AND getter
@synthesize shape = _shape;

- (void)setShape:(NSString *)shape
{
    if ([[SetCard validShapes] containsObject:shape]) {
        _shape = shape;
    }
}

- (NSString *)shape
{
    return _shape ? _shape : @"?";
}

@end
