#define CATCH_CONFIG_MAIN

#include "catch.hpp"
#include "di_container.h"


// простой пример
class Thing { };
class ThingConfig { };

class DoThingPipeline {
public:
    virtual Thing completeDoingThing(const ThingConfig& config) {
        return Thing();
    }
};

Thing makeSpecificThing(const DI& c)
{
    ThingConfig config;
    return c.getDoThingPipeline().completeDoingThing(config);
}


class FakeDoThingPipeline : public DoThingPipeline {
public:
    Thing completeDoingThing(const ThingConfig& thingConfig) override
    {
        completeDoingThingCalls++;
        return Thing();
    }

public:
    int completeDoingThingCalls = 0;
};


TEST_CASE("Make specific thing does the thing") {
    DI  container;
    auto doThingPipeline = container.installDoThingPipeline< FakeDoThingPipeline >();

    Thing thing = makeSpecificThing(container);
    REQUIRE(1 == doThingPipeline.getComponent().completeDoingThingCalls);
}


class PowerFinder {
public:
    int calculatePower(int base, unsigned exponent) const
    {
        return exponent == 0
            ? 1
            : calculatePower(base, exponent - 1) * base;
    }
};

class VirtualPowerFinder
{
public:
    virtual int calculatePower(int base, unsigned exponent) const
    {
        return exponent == 0
            ? 1
            : calculatePower(base, exponent - 1) * base;
    }
};

class StaticPowerFinder
{
public:
    static int calculatePower(int base, unsigned exponent)
    {
        return exponent == 0
            ? 1
            : calculatePower(base, exponent - 1) * base;
    }
};

uint64_t findSquares(const DI& c)
{
    uint64_t sum = 0;
    for (int i = 0; i < 100000; ++i)
        sum += c.getPowerFinder().calculatePower(i, 2);
    return sum;
}

uint64_t findSquaresVirtual(const DI& c)
{
    uint64_t sum = 0;
    for (int i = 0; i < 100000; ++i)
        sum += c.getVirtualPowerFinder().calculatePower(i, 2);
    return sum;
}

uint64_t findSquaresStatic(const DI& c)
{
    uint64_t sum = 0;
    for (int i = 0; i < 100000; ++i)
        sum += c.getStaticPowerFinder().calculatePower(i, 2);
    return sum;
}

TEST_CASE("Devirtualization of FindSquares", "[benchmark]")
{
    DI   container;
    auto powerFinder = container.installPowerFinder();
    auto virtualPowerFinder = container.installVirtualPowerFinder<VirtualPowerFinder>();
    auto staticPowerFinder = container.installStaticPowerFinder();

    SECTION("When the return values are captured.")
    {
        uint64_t total = 0;
        BENCHMARK("No virtual function calls involved.")
        {
            total = findSquares(container);
        }

        BENCHMARK("Virtual function calls involved.")
        {
            total = findSquaresVirtual(container);
        }

        BENCHMARK("Static function calls involved.")
        {
            total = findSquaresStatic(container);
        }

        REQUIRE(total == 18103503627376);
    }
    SECTION("When the return values are ignored.")
    {
        BENCHMARK("No virtual function calls involved.")
        {
            findSquares(container);
        }

        BENCHMARK("Virtual function calls involved.")
        {
            findSquaresVirtual(container);
        }

        BENCHMARK("Static function calls involved.")
        {
            findSquaresStatic(container);
        }
    }
}